package com.exasol.adapter.dialects.saphana;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.exasol.adapter.dialects.JDBCAdapterContext;

class SapHanaDialectFactoryTest {
    private SapHanaSqlDialectFactory factory;

    @BeforeEach
    void beforeEach() {
        this.factory = new SapHanaSqlDialectFactory();
    }

    @Test
    void testGetName() {
        assertThat(this.factory.getSqlDialectName(), equalTo("SAPHANA"));
    }

    @Test
    void testGetAdapterProjectShortTag() {
        assertThat(this.factory.getAdapterProjectShortTag(), equalTo("VSHANA"));
    }

    @Test
    void testGetSqlDialectVersion() {
        // Version only available in built artifact
        assertThat(this.factory.getSqlDialectVersion(), equalTo("UNKNOWN"));
    }

    @Test
    void testCreateDialect() {
        assertThat(this.factory.createSqlDialect(JDBCAdapterContext.builder().build()),
                instanceOf(SapHanaSqlDialect.class));
    }
}
