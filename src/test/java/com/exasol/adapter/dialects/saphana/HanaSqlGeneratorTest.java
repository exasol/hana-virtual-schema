package com.exasol.adapter.dialects.saphana;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.JDBCAdapterContext;
import com.exasol.adapter.sql.SqlLiteralBool;

class HanaSqlGeneratorTest {
    private HanaSqlGenerator sqlGenerator;

    @BeforeEach
    void beforeEach() {
        final SapHanaSqlDialect dialect = new SapHanaSqlDialect(JDBCAdapterContext.builder().properties(AdapterProperties.emptyProperties()).build());
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
