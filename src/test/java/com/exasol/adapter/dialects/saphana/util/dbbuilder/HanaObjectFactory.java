package com.exasol.adapter.dialects.saphana.util.dbbuilder;

import java.sql.Connection;

import com.exasol.dbbuilder.dialects.*;

/**
 * Factory for Hana top-level database objects.
 */
public final class HanaObjectFactory extends AbstractObjectFactory {
    private final HanaImmediateDatabaseObjectWriter writer;

    /**
     * Create a new {@link HanaObjectFactory} instance.
     *
     * @param connection JDBC connection
     */
    public HanaObjectFactory(final Connection connection) {
        this.writer = new HanaImmediateDatabaseObjectWriter(connection);
    }

    @Override
    public User createUser(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User createUser(final String name, final String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User createLoginUser(final String name) {
        return createUser(name);
    }

    @Override
    public User createLoginUser(final String name, final String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HanaSchema createSchema(final String name) {
        return new HanaSchema(this.writer, HanaIdentifier.of(name));
    }

    @Override
    protected DatabaseObjectWriter getWriter() {
        return this.writer;
    }
}