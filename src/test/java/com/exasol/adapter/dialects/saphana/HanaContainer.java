package com.exasol.adapter.dialects.saphana;

import java.nio.file.Path;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

import com.exasol.errorreporting.ExaError;

public class HanaContainer extends JdbcDatabaseContainer<HanaContainer> {
    public static final int HANA_PORT = 39041;
    public static final String DRIVER_CLASS = "com.sap.db.jdbc.Driver";
    private static final String PASSWORD = "Chutie2eiMu0ah0Gie1B";
    private static final int T_20_MINUTES = 20 * 60;

    public HanaContainer() {
        super(DockerImageName.parse("store/saplabs/hanaexpress:2.00.045.00.20200121.1"));
        if (!Path.of(System.getProperty("user.home")).resolve(".agree-to-sap-license").toFile().exists()) {
            throw new IllegalStateException(ExaError.messageBuilder("E-VSHA-1")
                    .message("Could not find ~/.agree-to-sap-license.")
                    .mitigation(
                            "Please read the SAP Developer Center Software Developer License Agreement and in case you agree create the file ~/.agree-to-sap-license. The content of the file is not relevant. You can leave it empty.")
                    .toString());
        }
        withConnectTimeoutSeconds(T_20_MINUTES);
        setCommand("--master-password " + PASSWORD + " --agree-to-sap-license");
    }

    @Override
    public String getDriverClassName() {
        return DRIVER_CLASS;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:sap:" + getContainerIpAddress() + ":" + getMappedPort(HANA_PORT);
    }

    @Override
    public String getUsername() {
        return "SYSTEM";
    }

    @Override
    public String getPassword() {
        return PASSWORD;
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1 FROM SYS.ABSTRACT_SQL_PLAN_DATA_";
    }

    @Override
    public void stop() {
        if (isShouldBeReused() && TestcontainersConfiguration.getInstance().environmentSupportsReuse()) {
            logger().warn(
                    "Leaving container running since reuse is enabled. Don't forget to stop and remove the container manually using docker rm -f CONTAINER_ID.");
        } else {
            super.stop();
        }
    }
}
