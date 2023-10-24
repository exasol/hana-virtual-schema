package com.exasol.adapter.dialects.saphana.util;

import static com.exasol.adapter.dialects.saphana.util.IntegrationTestConstants.*;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeoutException;

import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;

import com.exasol.adapter.dialects.saphana.util.dbbuilder.HanaObjectFactory;
import com.exasol.adapter.dialects.saphana.util.dbbuilder.HanaSchema;
import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolService;
import com.exasol.dbbuilder.dialects.Schema;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

public class IntegrationTestSetup implements AutoCloseable {
    private final HanaContainer<?> hanaContainer;
    private final ExasolContainer<?> exasolContainer;
    private final Connection exasolConnection;
    private final Connection hanaConnection;
    private final ExasolObjectFactory exasolFactory;
    private final HanaObjectFactory hanaFactory;
    private final AdapterScript adapterScript;
    private final ConnectionDefinition connectionDefinition;
    private int virtualSchemaCounter = 0;
    private int hanaSchemaCounter = 0;

    public IntegrationTestSetup(final HanaContainer<?> hana, final ExasolContainer<?> exasol)
            throws NoDriverFoundException, SQLException {
        this.hanaContainer = hana;
        this.exasolContainer = exasol;
        this.exasolConnection = this.createExasolConnection();
        this.hanaConnection = DriverManager.getConnection(this.hanaContainer.getJdbcUrl(),
                this.hanaContainer.getUsername(), this.hanaContainer.getPassword());
        this.exasolFactory = createExasolObjectFactory();
        this.hanaFactory = new HanaObjectFactory(this.hanaConnection);
        final ExasolSchema exasolSchema = this.exasolFactory.createSchema(SCHEMA_EXASOL);

        this.adapterScript = createAdapterScript(exasolSchema);
        final String connectionString = "jdbc:sap://" + this.exasolContainer.getHostIp() + ":"
                + this.hanaContainer.getSystemPort() + "/";
        this.connectionDefinition = this.exasolFactory.createConnectionDefinition("HANA_CONNECTION", connectionString,
                this.hanaContainer.getUsername(), this.hanaContainer.getPassword());
    }

    private ExasolObjectFactory createExasolObjectFactory() {
        final ExasolObjectConfiguration.Builder builder = ExasolObjectConfiguration.builder();
        final UdfTestSetup udfTestSetup = new UdfTestSetup(getTestHostIpFromInsideExasol(),
                this.exasolContainer.getDefaultBucket(), this.exasolConnection);
        builder.withJvmOptions(udfTestSetup.getJvmOptions());
        return new ExasolObjectFactory(this.exasolConnection, builder.build());
    }

    public static IntegrationTestSetup start() {
        final HanaContainer<?> hana = new HanaContainer<>(HANA_CONTAINER_VERSION).withReuse(true);
        final ExasolContainer<?> exasol = new ExasolContainer<>().withReuse(true)
                .withRequiredServices(ExasolService.BUCKETFS, ExasolService.UDF);
        hana.start();
        exasol.start();
        uploadDriverToBucket(exasol.getDefaultBucket());
        uploadVsJarToBucket(exasol.getDefaultBucket());
        try {
            return new IntegrationTestSetup(hana, exasol);
        } catch (NoDriverFoundException | SQLException exception) {
            throw new IllegalStateException("Failed to start integration test setup", exception);
        }
    }

    private static void uploadDriverToBucket(final Bucket bucket) {
        final String pathInBucket = "drivers/jdbc/" + JDBC_DRIVER_NAME;
        try {
            bucket.uploadStringContent(JDBC_DRIVER_CONFIGURATION_FILE_CONTENT,
                    "drivers/jdbc/" + JDBC_DRIVER_CONFIGURATION_FILE_NAME);
            bucket.uploadFile(JDBC_DRIVER_PATH, pathInBucket);
        } catch (final BucketAccessException | FileNotFoundException | InterruptedException
                | TimeoutException exception) {
            throw new IllegalStateException(
                    "Failed to upload JDBC driver from " + JDBC_DRIVER_PATH.toAbsolutePath() + " to " + pathInBucket,
                    exception);
        }
    }

    private static void uploadVsJarToBucket(final Bucket bucket) {
        try {
            bucket.uploadFile(PATH_TO_VIRTUAL_SCHEMAS_JAR, VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
        } catch (final FileNotFoundException | BucketAccessException | TimeoutException exception) {
            throw new IllegalStateException("Failed to upload jar " + PATH_TO_VIRTUAL_SCHEMAS_JAR + " to "
                    + VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION, exception);
        }
    }

    private String getTestHostIpFromInsideExasol() {
        final Map<String, ContainerNetwork> networks = this.exasolContainer.getContainerInfo().getNetworkSettings()
                .getNetworks();
        if (networks.size() == 0) {
            return null;
        }
        return networks.values().iterator().next().getGateway();
    }

    private AdapterScript createAdapterScript(final ExasolSchema schema) {
        final String content = "%scriptclass com.exasol.adapter.RequestDispatcher;\n" //
                + "%jar /buckets/bfsdefault/default/" + VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION + ";\n" //
                + "%jar /buckets/bfsdefault/default/drivers/jdbc/" + JDBC_DRIVER_NAME + ";\n";
        return schema.createAdapterScript(ADAPTER_SCRIPT_EXASOL, AdapterScript.Language.JAVA, content);
    }

    public VirtualSchema createVirtualSchema(final Schema hanaSchema) {
        final Map<String, String> properties = new HashMap<>(Map.of("CATALOG_NAME", hanaSchema.getName()));
        properties.putAll(debugProperties());
        return this.exasolFactory.createVirtualSchemaBuilder("HANA_VIRTUAL_SCHEMA_" + (this.virtualSchemaCounter++))
                .adapterScript(this.adapterScript).connectionDefinition(this.connectionDefinition)
                .sourceSchemaName(hanaSchema.getName()).properties(properties).build();
    }

    private Map<String, String> debugProperties() {
        final String debugAddress = System.getProperty("com.exasol.virtualschema.debug.address");
        if (debugAddress == null) {
            return Collections.emptyMap();
        }
        final String logLevel = System.getProperty("com.exasol.virtualschema.debug.level");
        return Map.of("DEBUG_ADDRESS", debugAddress, "LOG_LEVEL", (logLevel != null ? logLevel : "ALL"));
    }

    public HanaSchema createHanaSchema() {
        final String newSchemaName = "HANA_SCHEMA_" + this.hanaSchemaCounter++;
        if (hanaSchemaExists(newSchemaName)) {
            executeInHana("DROP SCHEMA " + newSchemaName + " CASCADE");
        }
        return this.hanaFactory.createSchema(newSchemaName);
    }

    public void clean(final Schema hanaSchema) {
        if (hanaSchemaExists(hanaSchema.getName())) {
            hanaSchema.drop();
        }
    }

    private boolean hanaSchemaExists(final String schemaName) {
        try (ResultSet rs = executeInHana(
                "SELECT COUNT(1) FROM SYS.SCHEMAS WHERE SCHEMA_NAME='" + schemaName + "' LIMIT 1")) {
            if (!rs.next()) {
                return false;
            }
            return rs.getInt(1) > 0;
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to check if HANA schema exists", exception);
        }
    }

    public ResultSet executeInHana(final String statement) {
        return execute(this.hanaConnection, statement);
    }

    public ResultSet executeInExasol(final String statement) {
        return execute(this.exasolConnection, statement);
    }

    private static ResultSet execute(final Connection connection, final String statement) {
        try {
            final Statement stmt = connection.createStatement();
            if (stmt.execute(statement)) {
                return stmt.getResultSet();
            } else {
                return null;
            }
        } catch (final SQLException exception) {
            throw new IllegalStateException("Failed to execute statement '" + statement + "'", exception);
        }
    }

    @Override
    public void close() throws SQLException {
        this.exasolConnection.close();
        this.hanaConnection.close();
        this.exasolContainer.close();
        this.hanaContainer.close();
    }

    public Connection createExasolConnection() throws NoDriverFoundException, SQLException {
        return this.exasolContainer.createConnection("");
    }
}
