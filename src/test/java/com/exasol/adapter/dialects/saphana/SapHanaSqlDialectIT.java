package com.exasol.adapter.dialects.saphana;

import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.*;
import java.nio.file.Path;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.containers.ExasolContainer;
import com.exasol.dbbuilder.dialects.*;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.dbbuilder.dialects.saphana.HanaObjectFactory;
import com.exasol.dbbuilder.dialects.saphana.HanaSchema;
import com.exasol.udfdebugging.UdfTestSetup;
import com.github.dockerjava.api.model.ContainerNetwork;

@Testcontainers
class SapHanaSqlDialectIT {

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
    protected static ExasolObjectFactory exasolObjectFactory;
    protected static HanaObjectFactory hanaObjectFactory;
    protected static Connection connection;
    protected static AdapterScript adapterScript;
    private static ExasolSchema adapterSchema;
    protected HanaSchema sourceSchema;
    protected VirtualSchema virtualSchema;
    private ConnectionDefinition jdbcConnection;

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
        bucket.uploadInputStream(() -> SapHanaSqlDialectIT.class.getClassLoader().getResourceAsStream("settings.cfg"),
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

    @AfterAll
    static void afterAll() throws SQLException {
        dropAll(adapterScript, adapterSchema);
        adapterScript = null;
        adapterSchema = null;
        connection.close();
    }

    /**
     * Drop all given database object if it is not already assigned to {@code null}.
     * <p>
     * The method is {@code static} so that it can be used in {@code afterAll()} too.
     * </p>
     *
     * @param databaseObjects database objects to be dropped
     */
    protected static void dropAll(final DatabaseObject... databaseObjects) {
        for (final DatabaseObject databaseObject : databaseObjects) {
            if (databaseObject != null) {
                databaseObject.drop();
            }
        }
    }

    @BeforeEach
    void beforeEach() {
        this.sourceSchema = hanaObjectFactory.createSchema(SOURCE_SCHEMA);
        this.jdbcConnection = createAdapterConnectionDefinition();
        this.virtualSchema = null;
    }

    private ConnectionDefinition createAdapterConnectionDefinition() {
        final String jdbcUrl = "jdbc:sap:" + getTestHostIpFromInsideExasol() + ":"
                + HANA.getMappedPort(HanaContainer.HANA_PORT);
        return exasolObjectFactory.createConnectionDefinition("JDBC_CONNECTION", jdbcUrl, HANA.getUsername(),
                HANA.getPassword());
    }

    @AfterEach
    void afterEach() {
        dropAll(this.virtualSchema, this.jdbcConnection, this.sourceSchema);
        this.virtualSchema = null;
        this.jdbcConnection = null;
        this.sourceSchema = null;
    }

    protected Table createSingleColumnTable(final String sourceType) {
        final String typeAsIdentifier = sourceType.replaceAll("[ ,]", "_").replaceAll("[()]", "");
        return this.sourceSchema.createTable("SINGLE_COLUMN_TABLE_" + typeAsIdentifier, "C1", sourceType);
    }

    protected void assertVirtualTableContents(final Table table, final Matcher<ResultSet> matcher) {
        final VirtualSchema virtualSchema = createVirtualSchema(this.sourceSchema);
        try {
            assertThat(selectAllFromCorrespondingVirtualTable(virtualSchema, table), matcher);
        } catch (final SQLException exception) {
            fail("Unable to execute assertion query. Caused by: " + exception.getMessage(), exception);
        } finally {
            virtualSchema.drop();
        }
    }

    protected VirtualSchema createVirtualSchema(final Schema sourceSchema) {
        return exasolObjectFactory.createVirtualSchemaBuilder(VS_NAME).dialectName(HANA_DIALECT) //
                .sourceSchema(sourceSchema) //
                .adapterScript(adapterScript) //
                .connectionDefinition(this.jdbcConnection) //
                .properties(Map.of()) //
                .build();
    }

    private ResultSet selectAllFromCorrespondingVirtualTable(final VirtualSchema virtualSchema, final Table table)
            throws SQLException {
        return selectAllFrom(getVirtualTableName(virtualSchema, table));
    }

    private ResultSet selectAllFrom(final String tableName) throws SQLException {
        return query("SELECT * FROM " + tableName);
    }

    private String getVirtualTableName(final VirtualSchema virtualSchema, final Table table) {
        return virtualSchema.getFullyQualifiedName() + ".\"" + table.getName() + "\"";
    }

    protected ResultSet query(final String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    @Test
    void testVarcharMappingUtf8() {
        final Table table = createSingleColumnTable("NVARCHAR(20)").insert("Hello world!").insert("Grüße!");
        assertVirtualTableContents(table, table("VARCHAR").row("Hello world!").row("Grüße!").matches());
    }

    /**
     * Test that the adapter maps the data types correct.
     * <p>
     * Hana Data Type reference:
     * https://help.sap.com/viewer/4fe29514fd584807ac9f2a04f6754767/LATEST/en-US/20a1569875191014b507cf392724b7eb.html
     * </p>
     */
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { "TINYINT; DECIMAL(3,0)", //
            "SMALLINT; DECIMAL(5,0)", //
            "INTEGER; DECIMAL(10,0)", //
            "BIGINT; DECIMAL(19,0)", //
            "SMALLDECIMAL; DECIMAL(16,0)", //
            "DECIMAL(10,2); DECIMAL(10,2)", //
            "DECIMAL(38,2); DECIMAL(38,2)", //
            "DECIMAL; DECIMAL(34,0)", "REAL; DOUBLE", //
            "DOUBLE; DOUBLE", //
            "BOOLEAN; BOOLEAN", //
            "FLOAT(40); DOUBLE", //
            "VARCHAR(20); VARCHAR(20) ASCII", //
            "NVARCHAR(20); VARCHAR(20) ASCII", // wrong
            "ALPHANUM(10); VARCHAR(10) ASCII", //
            "SHORTTEXT(30); VARCHAR(30) UTF8" })
    void testDatatypeMapping(final String hanaType, final String expectedExasolType) throws SQLException {
        final Table table = createSingleColumnTable(hanaType);
        final VirtualSchema virtualSchema = createVirtualSchema(this.sourceSchema);
        try (final ResultSet resultSet = query("SELECT COLUMN_TYPE FROM SYS.EXA_ALL_COLUMNS WHERE COLUMN_SCHEMA = '"
                + VS_NAME + "' AND COLUMN_TABLE = '" + table.getName() + "';")) {
            assertThat(resultSet, table().row(expectedExasolType).matches());
        } finally {
            virtualSchema.drop();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "VARBINARY", "BLOB", "CLOB", "NCLOB", "INTEGER ARRAY", "TEXT", "ST_GEOMETRY", "ST_POINT" })
    void testUnsupportedDatatypeMapping(final String hanaType) throws SQLException {
        final Table table = createSingleColumnTable(hanaType);
        final VirtualSchema virtualSchema = createVirtualSchema(this.sourceSchema);
        try (final ResultSet resultSet = query("SELECT COLUMN_TYPE FROM SYS.EXA_ALL_COLUMNS WHERE COLUMN_SCHEMA = '"
                + VS_NAME + "' AND COLUMN_TABLE = '" + table.getName() + "';")) {
            assertThat(resultSet, table("VARCHAR").matches());
        } finally {
            virtualSchema.drop();
        }
    }
}
