package com.exasol.adapter.dialects.saphana.util.dbbuilder;

import java.sql.Connection;

import com.exasol.dbbuilder.dialects.*;

/**
 * Database object writer that writes objects to the database immediately.
 */
public class HanaImmediateDatabaseObjectWriter extends AbstractImmediateDatabaseObjectWriter {

    /**
     * Create a new instance of an {@link HanaImmediateDatabaseObjectWriter}.
     *
     * @param connection JDBC connection
     */
    public HanaImmediateDatabaseObjectWriter(final Connection connection) {
        super(connection);
    }

    @Override
    public void write(final User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(final User user, final GlobalPrivilege... privileges) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getQuotedColumnName(final String columnName) {
        return HanaIdentifier.of(columnName).quote();
    }

    @Override
    public void write(final User user, final DatabaseObject object, final ObjectPrivilege... privileges) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drop(final Schema schema) {
        writeToObject(schema, "DROP SCHEMA " + schema.getFullyQualifiedName() + " CASCADE");
    }
}