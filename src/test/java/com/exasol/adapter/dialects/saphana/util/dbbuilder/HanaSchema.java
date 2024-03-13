package com.exasol.adapter.dialects.saphana.util.dbbuilder;

import com.exasol.db.Identifier;
import com.exasol.dbbuilder.dialects.AbstractSchema;
import com.exasol.dbbuilder.dialects.DatabaseObjectWriter;

/**
 * Hana database schema.
 */
public class HanaSchema extends AbstractSchema {
    private final HanaImmediateDatabaseObjectWriter writer;

    /**
     * Create a new database schema.
     *
     * @param writer database object writer
     * @param name   name of the database schema
     */
    public HanaSchema(final HanaImmediateDatabaseObjectWriter writer, final Identifier name) {
        super(name);
        this.writer = writer;
        getWriter().write(this);
    }

    @Override
    public DatabaseObjectWriter getWriter() {
        verifyNotDeleted();
        return this.writer;
    }

    @Override
    protected Identifier getIdentifier(final String name) {
        return HanaIdentifier.of(name);
    }
}