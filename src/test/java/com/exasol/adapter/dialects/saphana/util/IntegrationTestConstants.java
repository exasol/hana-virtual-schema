package com.exasol.adapter.dialects.saphana.util;

import java.nio.file.Path;

public final class IntegrationTestConstants {
    public static final String VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION = "virtual-schema-dist-14.0.4-hana-4.0.1.jar";
    public static final Path PATH_TO_VIRTUAL_SCHEMAS_JAR = Path.of("target", VIRTUAL_SCHEMAS_JAR_NAME_AND_VERSION);
    // https://hub.docker.com/r/saplabs/hanaexpress/tags
    public static final String HANA_CONTAINER_VERSION = "2.00.088.00.20251110.1";
    public static final String SCHEMA_EXASOL = "SCHEMA_EXASOL";
    public static final String ADAPTER_SCRIPT_EXASOL = "ADAPTER_SCRIPT_EXASOL";
    public static final String DOCKER_IP_ADDRESS = "172.17.0.1";

    public static final String JDBC_DRIVER_NAME = "ngdbc.jar";
    public static final Path JDBC_DRIVER_PATH = Path.of("target", "hana-driver", JDBC_DRIVER_NAME);

    private IntegrationTestConstants() {
        // intentionally left empty
    }
}
