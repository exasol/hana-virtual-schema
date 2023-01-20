package com.exasol.adapter.dialects.saphana;

import static com.exasol.matcher.ResultSetStructureMatcher.table;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.adapter.dialects.saphana.util.IntegrationTestSetup;
import com.exasol.dbbuilder.dialects.exasol.VirtualSchema;

@Tag("integration")
class HanaDialectIT {

    static IntegrationTestSetup SETUP = IntegrationTestSetup.start();

    @BeforeEach
    void afterEach() {
        SETUP.clean();
    }

    @AfterAll
    static void afterAll() throws IOException, SQLException {
        SETUP.close();
    }

    private String createSingleColumnTable(final String sourceType) {
        final String typeAsIdentifier = sourceType.replaceAll("[ ,]", "_").replaceAll("[()]", "");
        final String tableName = "SINGLE_COLUMN_TABLE_" + typeAsIdentifier;
        SETUP.executeInHana(
                "CREATE TABLE " + IntegrationTestSetup.HANA_SCHEMA + "." + tableName + "(C1 " + sourceType + ")");
        return tableName;
    }

    protected void assertVirtualTableContents(final String tableName, final Matcher<ResultSet> matcher) {
        final VirtualSchema virtualSchema = SETUP.createVirtualSchema();
        try {
            assertThat(selectAllFromCorrespondingVirtualTable(virtualSchema, tableName), matcher);
        } catch (final SQLException exception) {
            fail("Unable to execute assertion query. Caused by: " + exception.getMessage(), exception);
        } finally {
            virtualSchema.drop();
        }
    }

    private ResultSet selectAllFromCorrespondingVirtualTable(final VirtualSchema virtualSchema, final String tableName)
            throws SQLException {
        return selectAllFrom(getVirtualTableName(virtualSchema, tableName));
    }

    private ResultSet selectAllFrom(final String tableName) throws SQLException {
        return SETUP.executeInExasol("SELECT * FROM " + tableName);
    }

    private String getVirtualTableName(final VirtualSchema virtualSchema, final String tableName) {
        return virtualSchema.getFullyQualifiedName() + ".\"" + tableName + "\"";
    }

    @Test
    void testVarcharMappingUtf8() {
        // FIXME
        // final Table table = createSingleColumnTable("NVARCHAR(20)").insert("Hello world!").insert("Grüße!");
        // assertVirtualTableContents(table, table("VARCHAR").row("Hello world!").row("Grüße!").matches());
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
        final String tableName = createSingleColumnTable(hanaType);
        final VirtualSchema virtualSchema = SETUP.createVirtualSchema();
        try (final ResultSet resultSet = SETUP
                .executeInExasol("SELECT COLUMN_TYPE FROM SYS.EXA_ALL_COLUMNS WHERE COLUMN_SCHEMA = '"
                        + virtualSchema.getName() + "' AND COLUMN_TABLE = '" + tableName + "';")) {
            assertThat(resultSet, table().row(expectedExasolType).matches());
        } finally {
            virtualSchema.drop();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "VARBINARY", "BLOB", "CLOB", "NCLOB", "INTEGER ARRAY", "TEXT", "ST_GEOMETRY", "ST_POINT" })
    void testUnsupportedDatatypeMapping(final String hanaType) throws SQLException {
        final String tableName = createSingleColumnTable(hanaType);
        final VirtualSchema virtualSchema = SETUP.createVirtualSchema();
        try (final ResultSet resultSet = SETUP
                .executeInExasol("SELECT COLUMN_TYPE FROM SYS.EXA_ALL_COLUMNS WHERE COLUMN_SCHEMA = '"
                        + virtualSchema.getName() + "' AND COLUMN_TABLE = '" + tableName + "';")) {
            assertThat(resultSet, table("VARCHAR").matches());
        } finally {
            virtualSchema.drop();
        }
    }
}
