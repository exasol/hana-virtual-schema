package com.exasol.adapter.dialects.saphana.util;

import java.nio.file.Path;

public final class IntegrationTestConstants {
    public static final String VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION = "virtual-schema-dist-11.0.2-hana-2.1.1.jar";
    public static final Path PATH_TO_VIRTUAL_SCHEMAS_JAR = Path.of("target", VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    public static final String HANA_CONTAINER_VERSION = "2.00.061.00.20220519.1";
    public static final String EXASOL_CONTAINER_VERSION = "7.1.17";
    public static final String SCHEMA_EXASOL = "SCHEMA_EXASOL";
    public static final String ADAPTER_SCRIPT_EXASOL = "ADAPTER_SCRIPT_EXASOL";
    public static final String DOCKER_IP_ADDRESS = "172.17.0.1";

    public static final String JDBC_DRIVER_NAME = "ngdbc.jar";
    public static final Path JDBC_DRIVER_PATH = Path.of("target", "hana-driver", JDBC_DRIVER_NAME);
    public static final String JDBC_DRIVER_CONFIGURATION_FILE_NAME = "settings.cfg";
    public static final String JDBC_DRIVER_CONFIGURATION_FILE_CONTENT = "DRIVERNAME=HANA\n" //
            + "JAR=" + JDBC_DRIVER_NAME + "\n" //
            + "DRIVERMAIN=com.sap.db.jdbc.Driver\n" //
            + "PREFIX=jdbc:sap:\n" //
            + "NOSECURITY=YES\n" //
            + "FETCHSIZE=100000\n" //
            + "INSERTSIZE=-1\n";

    private IntegrationTestConstants() {
        // intentionally left empty
    }
}
