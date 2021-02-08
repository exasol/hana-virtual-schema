package com.exasol.adapter.dialects.saphana;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.sql.SqlLiteralBool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class HanaSqlGeneratorTest {
    private HanaSqlGenerator sqlGenerator;

    @BeforeEach
    void beforeEach() {
        final SapHanaSqlDialect dialect = new SapHanaSqlDialect(null, AdapterProperties.emptyProperties());
        this.sqlGenerator = new HanaSqlGenerator(dialect, null);
    }

    @Test
    void testVisitLiteralBoolTrue() {
        assertThat(this.sqlGenerator.visit(new SqlLiteralBool(true)), equalTo("1 = 1"));
    }

    @Test
    void testVisitLiteralBoolFalse() {
        assertThat(this.sqlGenerator.visit(new SqlLiteralBool(false)), equalTo("1 = 0"));
    }
}