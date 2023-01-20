package com.exasol.adapter.dialects.saphana.util;

import static com.exasol.adapter.dialects.saphana.util.IntegrationTestConstants.*;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.containers.ExasolService;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

public class IntegrationTestSetup implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(IntegrationTestSetup.class.getName());
    public final static String HANA_SCHEMA = "HANA_SOURCE_SCHEMA";

    private final HanaContainer<?> hanaContainer;
    private final ExasolContainer<?> exasolContainer;
    private final Connection exasolConnection;
    private final Connection hanaConnection;
    private final ExasolObjectFactory exasolFactory;
    private final AdapterScript adapterScript;
    private final ConnectionDefinition connectionDefinition;
    private int virtualSchemaCounter = 0;

    public IntegrationTestSetup(final HanaContainer<?> hana, final ExasolContainer<?> exasol)
            throws NoDriverFoundException, SQLException {
        this.hanaContainer = hana;
        this.exasolContainer = exasol;
        this.exasolConnection = this.exasolContainer.createConnection("");
        this.hanaConnection = DriverManager.getConnection(this.hanaContainer.getJdbcUrl(), hanaContainer.getUsername(),
                hanaContainer.getPassword());
        final ExasolObjectConfiguration.Builder builder = ExasolObjectConfiguration.builder();
        final UdfTestSetup udfTestSetup = new UdfTestSetup(getTestHostIpFromInsideExasol(),
                this.exasolContainer.getDefaultBucket(), this.exasolConnection);
        builder.withJvmOptions(udfTestSetup.getJvmOptions());
        this.exasolFactory = new ExasolObjectFactory(this.exasolConnection, builder.build());
        final ExasolSchema exasolSchema = this.exasolFactory.createSchema(SCHEMA_EXASOL);

        this.adapterScript = createAdapterScript(exasolSchema);
        final String connectionString = "jdbc:sap://" + this.exasolContainer.getHostIp() + ":"
                + this.hanaContainer.getSystemPort() + "/";
        this.connectionDefinition = this.exasolFactory.createConnectionDefinition("HANA_CONNECTION", connectionString,
                this.hanaContainer.getUsername(), this.hanaContainer.getPassword());
    }

    public static IntegrationTestSetup start() {
        final HanaContainer<?> hana = new HanaContainer<>("saplabs/hanaexpress:2.00.061.00.20220519.1").withReuse(true);
        final ExasolContainer<?> exasol = new ExasolContainer<>("7.1.17")
                .withRequiredServices(ExasolService.BUCKETFS, ExasolService.UDF).withReuse(true);
        hana.start();
        exasol.start();
        final Bucket bucket = exasol.getDefaultBucket();
        uploadDriverToBucket(bucket);
        uploadVsJarToBucket(bucket);
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

    public VirtualSchema createVirtualSchema() {
        final Map<String, String> properties = new HashMap<>(Map.of("CATALOG_NAME", HANA_SCHEMA));
        properties.putAll(Map.of("DEBUG_ADDRESS", "192.168.56.7", "LOG_LEVEL", "ALL"));
        return this.exasolFactory.createVirtualSchemaBuilder("HANA_VIRTUAL_SCHEMA_" + (this.virtualSchemaCounter++))
                .adapterScript(this.adapterScript).connectionDefinition(this.connectionDefinition)
                .sourceSchemaName(HANA_SCHEMA).properties(properties).build();
    }

    public void clean() {
        executeInHana("DROP SCHEMA " + HANA_SCHEMA + " CASCADE");
        executeInHana("CREATE SCHEMA " + HANA_SCHEMA);
    }

    public ResultSet executeInHana(final String statement) {
        return execute(hanaConnection, statement);
    }

    public ResultSet executeInExasol(final String statement) {
        return execute(exasolConnection, statement);
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
        exasolConnection.close();
        hanaConnection.close();
        exasolContainer.close();
        hanaContainer.close();
    }
}
