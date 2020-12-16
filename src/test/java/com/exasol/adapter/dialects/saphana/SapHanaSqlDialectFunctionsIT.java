package com.exasol.adapter.dialects.saphana;

import static com.exasol.adapter.capabilities.ScalarFunctionCapability.*;
import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
import com.exasol.matcher.FuzzyCellMatcher;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

@Testcontainers
@Execution(value = ExecutionMode.CONCURRENT) // use -Djunit.jupiter.execution.parallel.enabled=true to
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
            SESSION_PARAMETER, RAND, CURRENT_USER, ADD, SUB, MULT, LOCALTIMESTAMP, JSON_VALUE, EXTRACT,
            NUMTOYMINTERVAL);
    private static final int MAX_NUM_PARAMETERS = 4;
    private static final List<String> LITERALS = List.of("0.5", "2", "TRUE", "'a'", "DATE '2007-03-31'",
            "TIMESTAMP '2007-03-31 12:59:30.123'", "INTERVAL '1 12:00:30.123' DAY TO SECOND", "'POINT (1 2)'",
            "'LINESTRING (0 0, 0 1, 1 1)'", "GEOMETRYCOLLECTION(POINT(2 5)",
            "POLYGON((5 1, 5 5, 9 7, 10 1, 5 1),(6 2, 6 3, 7 3, 7 2, 6 2))",
            "MULTIPOLYGON(((0 0, 0 2, 2 2, 3 1, 0 0)), ((4 6, 8 9, 12 5, 4 6), (8 6, 9 6, 9 7, 8 7, 8 6)))",
            "MULTILINESTRING((0 1, 2 3, 1 6), (4 4, 5 5))");
    private static final List<List<String>> PARAMETER_COMBINATIONS = generateParameterCombinations();
    protected static ExasolObjectFactory exasolObjectFactory;
    protected static HanaObjectFactory hanaObjectFactory;
    protected static Connection connection;
    protected static AdapterScript adapterScript;
    protected static HanaSchema sourceSchema;
    protected static VirtualSchema virtualSchema;
    private static ExasolSchema adapterSchema;
    private static ConnectionDefinition jdbcConnection;
    private static Table table;
    private static Statement statement;

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
        statement = connection.createStatement();
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
        return sourceSchema.createTable("SINGLE_COLUMN_TABLE", "C1", sourceType);
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

    private static List<List<String>> generateParameterCombinations() {
        final List<List<String>> combinations = new ArrayList<>(MAX_NUM_PARAMETERS + 1);
        combinations.add(List.of(""));
        for (int numParameters = 1; numParameters <= MAX_NUM_PARAMETERS; numParameters++) {
            final List<String> previousIterationParameters = combinations.get(numParameters - 1);
            combinations.add(previousIterationParameters.stream().flatMap(smallerCombination -> //
            LITERALS.stream()
                    .map(literal -> smallerCombination.isEmpty() || literal.isEmpty() ? smallerCombination + literal
                            : smallerCombination + ", " + literal)//
            ).collect(Collectors.toList()));
        }
        return combinations;
    }

    protected ResultSet query(final String sql) throws SQLException {
        return statement.executeQuery(sql);
    }

    @ParameterizedTest
    @MethodSource("getScalarFunctions")
    void testScalarFunctions(final ScalarFunctionCapability function) {
        final List<ExasolRun> successfulExasolRuns = findFittingParameters(function);
        if (successfulExasolRuns.isEmpty()) {
            throw new IllegalStateException("Non of the parameter combinations lead to a successful run.");
        } else {
            assertFunctionBehavesSameOnVs(function, successfulExasolRuns);
        }
    }

    private List<ExasolRun> findFittingParameters(final ScalarFunctionCapability function) {
        final int fastThreshold = 3; // with three parameters the search is still fast; with 4 it gets slow
        final List<List<String>> fastCombinationLists = PARAMETER_COMBINATIONS.subList(0, fastThreshold + 1);
        final List<ExasolRun> fastParameters = findFittingParameters(function,
                fastCombinationLists.stream().flatMap(Collection::stream));
        if (!fastParameters.isEmpty()) {
            return fastParameters;
        } else {
            for (int numParameters = fastThreshold + 1; numParameters <= MAX_NUM_PARAMETERS; numParameters++) {
                final List<ExasolRun> result = findFittingParameters(function,
                        PARAMETER_COMBINATIONS.get(numParameters).stream());
                if (!result.isEmpty()) {
                    return result;
                }
            }
            return Collections.emptyList();
        }
    }

    private List<ExasolRun> findFittingParameters(final ScalarFunctionCapability function,
            final Stream<String> possibleParameters) {
        return possibleParameters.map(parameterCombination -> this.runFunctionOnExasol(function, parameterCombination))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private ExasolRun runFunctionOnExasol(final ScalarFunctionCapability function, final String parameters) {
        try (final ResultSet expectedResult = query("SELECT " + function + "(" + parameters + ") FROM DUAL")) {
            expectedResult.next();
            return new ExasolRun(parameters, expectedResult.getObject(1));
        } catch (final SQLException exception) {
            return null;
        }
    }

    private void assertFunctionBehavesSameOnVs(final ScalarFunctionCapability function,
            final Collection<ExasolRun> runsOnExasol) {
        final String selectList = runsOnExasol.stream().map(run -> function + "(" + run.parameters + ")")
                .collect(Collectors.joining(", "));
        final String virtualSchemaQuery = "SELECT " + selectList + " FROM " + virtualSchema.getFullyQualifiedName()
                + "." + table.getName();
        try (final ResultSet actualResult = query(virtualSchemaQuery)) {
            assertThat(actualResult,
                    table().row(runsOnExasol.stream().map(ExasolRun::getResult).map(this::buildMatcher).toArray())
                            .matchesFuzzily());
        } catch (final SQLException exception) {
            fail("Virtual Schema query failed while Exasol query did not.", exception);
        }
    }

    private Matcher<Object> buildMatcher(final Object object) {
        if (object == null) {
            return nullValue();
        } else {
            return FuzzyCellMatcher.fuzzilyEqualTo(object, BigDecimal.valueOf(0.0001),
                    FuzzyCellMatcher.FuzzyMode.NO_TYPE_CHECK);
        }
    }

    private static class ExasolRun {
        private final String parameters;
        private final Object result;

        private ExasolRun(final String parameters, final Object result) {
            this.parameters = parameters;
            this.result = result;
        }

        public Object getResult() {
            return this.result;
        }
    }
}
