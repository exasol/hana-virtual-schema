package com.exasol.adapter.dialects.saphana;

import static com.exasol.adapter.capabilities.ScalarFunctionCapability.*;
import static com.exasol.matcher.ResultSetMatcher.matchesResultSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.*;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.capabilities.ScalarFunctionCapability;
import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.dbbuilder.dialects.Schema;
import com.exasol.dbbuilder.dialects.Table;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.dbbuilder.dialects.saphana.HanaObjectFactory;
import com.exasol.dbbuilder.dialects.saphana.HanaSchema;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

@Testcontainers
//@Execution(value = ExecutionMode.CONCURRENT)
class SapHanaSqlDialectFunctionsIT {

    public static final String VIRTUAL_SCHEMA_JAR_NAME_AND_VERSION = "virtual-schema-dist-7.0.0-hana-1.0.0.jar";
    public static final Path PATH_TO_VIRTUAL_SCHEMA_JAR = Path.of("target", VIRTUAL_SCHEMA_JAR_NAME_AND_VERSION);
    @Container
    protected static final ExasolContainer<? extends ExasolContainer<?>> EXASOL = new ExasolContainer<>("7.0.4")
            .withReuse(true);
    private static final String HANA_DRIVER_NAME = "ngdbc.jar";
    public static final Path PATH_TO_HANA_DRIVER = Path.of("target", "hana-driver", HANA_DRIVER_NAME);
    @Container
    private static final HanaContainer HANA = new HanaContainer().withReuse(true);
    private static final String HANA_DIALECT = "SAPHANA";
    private static final String JDBC_DRIVER_PREFIX = "drivers/jdbc/";
    private static final String VS_NAME = "THE_VS";
    private static final String SOURCE_SCHEMA = "SOURCE_SCHEMA";
    private static final Set<ScalarFunctionCapability> EXCLUDES = Set.of(CAST, CASE, FLOAT_DIV, NEG, POSIX_TIME,
            SESSION_PARAMETER, RAND, CURRENT_USER, ADD, SUB, MULT, LOCALTIMESTAMP);
    private static final int MAX_NUM_PARAMETERS = 2;
    private static final List<String> LITERALS = List.of("0.5", "2", "TRUE", "'a'", "DATE '2007-03-31'",
            "TIMESTAMP '2007-03-31 12:59:30.123'", "INTERVAL '1 12:00:30.123' DAY TO SECOND", "'POINT (1 2)'",
            "'LINESTRING (0 0, 0 1, 1 1)'", "GEOMETRYCOLLECTION(POINT(2 5)", "");
    private static final Set<String> PARAMETER_COMBINATIONS = iterate(MAX_NUM_PARAMETERS).collect(Collectors.toSet());
    protected static ExasolObjectFactory exasolObjectFactory;
    protected static HanaObjectFactory hanaObjectFactory;
    protected static Connection connection;
    protected static AdapterScript adapterScript;
    protected static HanaSchema sourceSchema;
    protected static VirtualSchema virtualSchema;
    private static ExasolSchema adapterSchema;
    private static ConnectionDefinition jdbcConnection;
    private static Table table;

    @BeforeAll
    static void beforeAll() throws BucketAccessException, InterruptedException, TimeoutException, IOException,
            JdbcDatabaseContainer.NoDriverFoundException, SQLException {
        connection = EXASOL.createConnection();
        exasolObjectFactory = setUpObjectFactory();
        final Connection hanaConnection = DriverManager.getConnection(HANA.getJdbcUrl(), HANA.getUsername(),
                HANA.getPassword());
        hanaObjectFactory = new HanaObjectFactory(hanaConnection);
        adapterSchema = exasolObjectFactory.createSchema("ADAPTER_SCHEMA");
        adapterScript = installVirtualSchemaAdapter(adapterSchema);
        createConnectToExasolScript();
        cleanHana(hanaConnection);
        sourceSchema = hanaObjectFactory.createSchema(SOURCE_SCHEMA);
        jdbcConnection = createAdapterConnectionDefinition();
        table = createSingleColumnTable("INTEGER").insert(0);
        virtualSchema = createVirtualSchema(sourceSchema);
    }

    // TODO refactor when https://github.com/exasol/exasol-testcontainers/issues/116 is solved
    private static void createConnectToExasolScript() throws IOException {
        final File connectToExasolScript = new File("connectToExasol.sh");
        try (final FileWriter myWriter = new FileWriter(connectToExasolScript)) {
            myWriter.write("docker exec -it " + EXASOL.getContainerId() + " exaplus -c localhost:8563" + " -u "
                    + EXASOL.getUsername() + " -p " + EXASOL.getPassword());
        }
        connectToExasolScript.setExecutable(true);
    }

    private static void cleanHana(final Connection hanaConnection) {
        try {
            hanaConnection.createStatement().executeUpdate("DROP SCHEMA " + SOURCE_SCHEMA + " CASCADE");
        } catch (final SQLException exception) {
            // dismiss; we just try to delete the schema and if it fails it probably did not exist
        }
    }

    private static ExasolObjectFactory setUpObjectFactory() {
        final UdfTestSetup udfTestSetup = new UdfTestSetup(getTestHostIpFromInsideExasol(), EXASOL.getDefaultBucket());
        return new ExasolObjectFactory(connection,
                ExasolObjectConfiguration.builder().withJvmOptions(udfTestSetup.getJvmOptions()).build());
    }

    private static String getTestHostIpFromInsideExasol() {
        final Map<String, ContainerNetwork> networks = EXASOL.getContainerInfo().getNetworkSettings().getNetworks();
        if (networks.size() == 0) {
            return null;
        }
        return networks.values().iterator().next().getGateway();
    }

    private static AdapterScript installVirtualSchemaAdapter(final ExasolSchema adapterSchema)
            throws InterruptedException, BucketAccessException, TimeoutException {
        final Bucket bucket = EXASOL.getDefaultBucket();
        // TODO refactor after https://github.com/exasol/exasol-testcontainers/issues/115 is fixed
        bucket.uploadInputStream(
                () -> SapHanaSqlDialectFunctionsIT.class.getClassLoader().getResourceAsStream("settings.cfg"),
                JDBC_DRIVER_PREFIX + "settings.cfg");
        bucket.uploadFile(PATH_TO_HANA_DRIVER, JDBC_DRIVER_PREFIX + HANA_DRIVER_NAME);
        bucket.uploadFile(PATH_TO_VIRTUAL_SCHEMA_JAR, VIRTUAL_SCHEMA_JAR_NAME_AND_VERSION);
        // TODO refactor when https://github.com/exasol/test-db-builder-java/issues/48 is fixed
        return adapterSchema.createAdapterScriptBuilder("HANA_ADAPTER") //
                .language(AdapterScript.Language.JAVA) //
                .content("%scriptclass com.exasol.adapter.RequestDispatcher;\n" + "%jar /buckets/bfsdefault/default/"
                        + JDBC_DRIVER_PREFIX + HANA_DRIVER_NAME + ";\n" + "%jar /buckets/bfsdefault/default/"
                        + VIRTUAL_SCHEMA_JAR_NAME_AND_VERSION + ";\n")
                .build();
    }

    private static ConnectionDefinition createAdapterConnectionDefinition() {
        final String jdbcUrl = "jdbc:sap:" + getTestHostIpFromInsideExasol() + ":"
                + HANA.getMappedPort(HanaContainer.HANA_PORT);
        return exasolObjectFactory.createConnectionDefinition("JDBC_CONNECTION", jdbcUrl, HANA.getUsername(),
                HANA.getPassword());
    }

    private static Table createSingleColumnTable(final String sourceType) {
        final String typeAsIdentifier = sourceType.replaceAll("[ ,]", "_").replaceAll("[()]", "");
        return sourceSchema.createTable("SINGLE_COLUMN_TABLE_" + typeAsIdentifier, "C1", sourceType);
    }

    private static VirtualSchema createVirtualSchema(final Schema sourceSchema) {
        return exasolObjectFactory.createVirtualSchemaBuilder(VS_NAME).dialectName(HANA_DIALECT) //
                .sourceSchema(sourceSchema) //
                .adapterScript(adapterScript) //
                .connectionDefinition(jdbcConnection) //
                .properties(Map.of()) //
                .build();
    }

    static Stream<Arguments> getScalarFunctions() {
        return Arrays.stream(ScalarFunctionCapability.values())//
                .filter(function -> !EXCLUDES.contains(function))//
                .map(Arguments::of);
    }

    private static Stream<String> iterate(final int size) {
        if (size == 0) {
            return Stream.of("");
        } else {
            return iterate(size - 1).flatMap(smallerCombination -> //
            LITERALS.stream()
                    .map(literal -> smallerCombination.isEmpty() || literal.isEmpty() ? smallerCombination + literal
                            : smallerCombination + ", " + literal)//
            );
        }
    }

    protected ResultSet query(final String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    @ParameterizedTest
    @MethodSource("getScalarFunctions")
    void testUnaryNumberScalaFunctions(final ScalarFunctionCapability function) {
        final AtomicBoolean hadASuccess = new AtomicBoolean(false);
        final Map<String, SQLException> fails = new HashMap<>();
        PARAMETER_COMBINATIONS.parallelStream().forEach(parameter -> {
            final SQLException exception = assertFunctionBehavesSameOnVsAndRealTable(function, parameter,
                    table.getName());
            if (exception == null) {
                hadASuccess.set(true);
                System.out.println("Succeeded with parameters: " + parameter);
            } else {
                fails.put(parameter, exception);
            }
        });
        if (!hadASuccess.get()) {
            fails.keySet().forEach(failedParameter -> {
                System.out.println(failedParameter + ":");
                fails.get(failedParameter).printStackTrace();
            });
            fail("Non of the parameter combinations lead to a fit. See cause for an example exception.");
        }
    }

    private SQLException assertFunctionBehavesSameOnVsAndRealTable(final ScalarFunctionCapability function,
            final String parameters, final String tableName) {
        final String virtualSchemaQuery = "SELECT " + function + "(" + parameters + ") FROM "
                + virtualSchema.getFullyQualifiedName() + "." + tableName;
        try (final ResultSet expectedResult = query("SELECT " + function + "(" + parameters + ") FROM " + tableName)) {
            assertVirtualSchemaFunction(virtualSchemaQuery, expectedResult);
            return null;
        } catch (final SQLException exception) {
            assertThrows(SQLException.class, () -> query(virtualSchemaQuery));
            return exception;
        }
    }

    private void assertVirtualSchemaFunction(final String virtualSchemaQuery, final ResultSet expectedResult) {
        try (final ResultSet actualResult = query(virtualSchemaQuery)) {
            assertThat(actualResult, matchesResultSet(expectedResult));
        } catch (final SQLException exception) {
            fail("Virtual Schema query failed while Exasol query did not.", exception);
        }
    }
}
