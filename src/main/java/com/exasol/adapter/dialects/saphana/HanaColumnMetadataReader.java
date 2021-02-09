package com.exasol.adapter.dialects.saphana;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.IdentifierConverter;
import com.exasol.adapter.jdbc.BaseColumnMetadataReader;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.metadata.DataType;

import java.sql.Connection;
import java.sql.Types;

import static com.exasol.adapter.metadata.DataType.ExaCharset.ASCII;
import static com.exasol.adapter.metadata.DataType.ExaCharset.UTF8;

/**
 * This class implements Hana-specific reading of column metadata.
 */
public class HanaColumnMetadataReader extends BaseColumnMetadataReader {

    /**
     * Create a new instance of the {@link HanaColumnMetadataReader}.
     *
     * @param connection          JDBC connection through which the column metadata is read from the remote database
     * @param properties          user-defined adapter properties
     * @param identifierConverter converter between source and Exasol identifiers
     */
    public HanaColumnMetadataReader(final Connection connection, final AdapterProperties properties,
            final IdentifierConverter identifierConverter) {
        super(connection, properties, identifierConverter);
    }

    @Override
    public DataType mapJdbcType(final JDBCTypeDescription jdbcTypeDescription) {
        switch (jdbcTypeDescription.getJdbcType()) {
            case Types.NVARCHAR:
                return convertNvarchar(jdbcTypeDescription.getPrecisionOrSize(), jdbcTypeDescription.getTypeName());
            case Types.VARCHAR:
                return DataType.createVarChar(jdbcTypeDescription.getPrecisionOrSize(), ASCII);
            default:
                return super.mapJdbcType(jdbcTypeDescription);
        }
    }

    private static DataType convertNvarchar(final int size, final String typeName) {
        if (typeName.equalsIgnoreCase("ALPHANUM")) {
            return DataType.createVarChar(size, ASCII);
        } else {
            return DataType.createVarChar(size, UTF8);
        }
    }
}