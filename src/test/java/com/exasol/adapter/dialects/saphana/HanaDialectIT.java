package com.exasol.adapter.dialects.saphana;

import java.sql.SQLException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer.NoDriverFoundException;

import com.exasol.adapter.dialects.saphana.util.IntegrationTestSetup;

@Tag("integration")
class HanaDialectIT {

    static IntegrationTestSetup SETUP = IntegrationTestSetup.start();

    @Test
    void test() throws NoDriverFoundException, SQLException {
        System.out.println("juhu");
    }
}
