package com.exasol.adapter.dialects.saphana.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

public class HanaContainer<SELF extends HanaContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    private static final String DB_DRIVER = "com.sap.db.jdbc.Driver";

    private static final Integer SYSTEM_PORT = 39017;
    private static final Integer TENANT_PORT = 39041;

    private static final String SYSTEM_DB_NAME = "SYSTEMDB";
    private static final String TENANT_DB_NAME = "HXE";
    private static final String USERNAME = "SYSTEM";
    private static final String PASSWORD = "HXEHana1";

    public HanaContainer(final String image) {
        super(DockerImageName.parse(image));
        addExposedPorts(SYSTEM_PORT, TENANT_PORT);
        this.withCommand("--master-password " + PASSWORD + " --agree-to-sap-license");
        this.waitStrategy = new LogMessageWaitStrategy().withRegEx(".*Startup finished!*\\s").withTimes(1)
                .withStartupTimeout(Duration.ofMinutes(12));
    }

    @Override
    protected Set<Integer> getLivenessCheckPorts() {
        return Set.of(getTenantPort(), getSystemPort());
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    @Override
    public String getDriverClassName() {
        return DB_DRIVER;
    }

    /**
     * Default database name getter. Because of HANAs two databases it will return the tenant database. If you want to
     * get the name of the system database, use 'getSystemDatabaseName'.
     * 
     * @return The name of the tenant database.
     */
    @Override
    public String getDatabaseName() {
        return getTenantDatabaseName();
    }

    /**
     * Get the name of the HANA system database.
     * 
     * @return The name of the HANA system database.
     */
    public String getSystemDatabaseName() {
        return SYSTEM_DB_NAME;
    }

    /**
     * Get the name of the HANA tenant database.
     * 
     * @return The name of the HANA tenant database.
     */
    public String getTenantDatabaseName() {
        return TENANT_DB_NAME;
    }

    @Override
    public String getUsername() {
        return USERNAME;
    }

    @Override
    public String getPassword() {
        return PASSWORD;
    }

    @Override
    public String getTestQueryString() {
        return "SELECT 1 FROM sys.dummy";
    }

    /**
     * Get the port for the system database and general connections.
     * 
     * @return port of the system database.
     */
    @NotNull
    public Integer getSystemPort() {
        return getMappedPort(SYSTEM_PORT);
    }

    /**
     * Get the port for the tenant database.
     * 
     * @return port of the tenant database.
     */
    @NotNull
    public Integer getTenantPort() {
        return getMappedPort(TENANT_PORT);
    }

    /**
     * Get the Jdbc url used to connect to the databases.
     * 
     * @return jdbc url
     */
    @Override
    public String getJdbcUrl() {
        return "jdbc:sap://" + getHost() + ":" + getSystemPort() + "/";
    }

    /**
     * Modification of the default connection string because of HANA specific database selection.
     * 
     * Query will connect to the tenant database per default. If you want to connect to the system database, supply
     * '?databaseName=SYSTEMDB' as queryString
     * 
     * @param queryString your custom query attached to the connection string.
     * @return Connection object.
     * 
     */
    @Override
    public Connection createConnection(String queryString) throws SQLException, NoDriverFoundException {

        if (queryString == null || queryString.trim().isEmpty()) {
            queryString = "?databaseName=" + TENANT_DB_NAME;
        } else {
            // Remove ? from queryString if supplied.
            if (Character.compare(queryString.charAt(0), '?') == 0) {
                queryString = queryString.substring(1);
            }
            queryString = "?databaseName=" + TENANT_DB_NAME + "&" + queryString;
        }

        return super.createConnection(queryString);
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
