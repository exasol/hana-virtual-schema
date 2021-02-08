package com.exasol.adapter.dialects.saphana;

import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.dialects.rewriting.SqlGenerationVisitor;
import com.exasol.adapter.sql.SqlLiteralBool;

/**
 * This class generates SQL queries for the {@link SapHanaSqlDialect}.
 */
public class HanaSqlGenerator extends SqlGenerationVisitor {
    /**
     * Create a new instance of the {@link HanaSqlGenerator}.
     *
     * @param dialect {@link SapHanaSqlDialect} SQL dialect
     * @param context SQL generation context
     */
    public HanaSqlGenerator(final SqlDialect dialect, final SqlGenerationContext context) {
        super(dialect, context);
    }

    @Override
    public String visit(final SqlLiteralBool literal) {
        return literal.getValue() ? "1 = 1" : "1 = 0";
    }
}