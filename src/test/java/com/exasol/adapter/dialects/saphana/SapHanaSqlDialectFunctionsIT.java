package com.exasol.adapter.dialects.saphana;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.*;

import com.exasol.adapter.commontests.scalarfunction.ScalarFunctionsTestBase;
import com.exasol.adapter.commontests.scalarfunction.TestSetup;
import com.exasol.adapter.commontests.scalarfunction.virtualschematestsetup.*;
import com.exasol.adapter.commontests.scalarfunction.virtualschematestsetup.request.Column;
import com.exasol.adapter.commontests.scalarfunction.virtualschematestsetup.request.TableRequest;
import com.exasol.adapter.dialects.saphana.util.IntegrationTestSetup;
import com.exasol.adapter.dialects.saphana.util.dbbuilder.HanaSchema;
import com.exasol.adapter.metadata.DataType;
import com.exasol.dbbuilder.dialects.Table;
import com.exasol.dbbuilder.dialects.exasol.VirtualSchema;

@Tag("integration")
@Disabled("Not yet working")
class SapHanaSqlDialectFunctionsIT extends ScalarFunctionsTestBase {

    static IntegrationTestSetup SETUP = IntegrationTestSetup.start();
    private static HanaSchema hanaSchema;

    @BeforeAll
    static void beforeEach() {
        hanaSchema = SETUP.createHanaSchema();
    }

    @Override
    protected TestSetup getTestSetup() {
        return new TestSetup() {
            @Override
            public Set<String> getDialectSpecificExcludes() {
                return Set.of();
            }

            @Override
            public VirtualSchemaTestSetupProvider getVirtualSchemaTestSetupProvider() {
                return new VirtualSchemaTestSetupProvider() {
                    @Override
                    public VirtualSchemaTestSetup createSingleTableVirtualSchemaTestSetup(
                            final CreateVirtualSchemaTestSetupRequest request) {
                        final Table table = createTable(request);
                        final VirtualSchema virtualSchema = SETUP.createVirtualSchema(hanaSchema);
                        return new VirtualSchemaTestSetup() {
                            @Override
                            public String getFullyQualifiedName() {
                                return virtualSchema.getFullyQualifiedName();
                            }

                            @Override
                            public void close() throws SQLException {
                                virtualSchema.drop();
                                table.drop();
                            }
                        };
                    }
                };
            }

            @Override
            public Connection createExasolConnection() throws SQLException {
                return SETUP.createExasolConnection();
            }

            @Override
            public String getExternalTypeFor(final DataType exasolType) {
                switch (exasolType.getExaDataType()) {
                case VARCHAR:
                    return "VARCHAR(" + exasolType.getSize() + ")";
                default:
                    return exasolType.toString();
                }
            }
        };
    }

    private static Table createTable(final CreateVirtualSchemaTestSetupRequest request) {
        assertEquals(1, request.getTableRequests().size());
        final TableRequest tableRequest = request.getTableRequests().get(0);
        final List<String> columnNames = tableRequest.getColumns().stream().map(Column::getName).collect(toList());
        final List<String> columnTypes = tableRequest.getColumns().stream().map(Column::getType).collect(toList());
        final Table table = hanaSchema.createTable(tableRequest.getName(), columnNames, columnTypes);
        return table;
    }
}
