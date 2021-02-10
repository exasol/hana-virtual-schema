package com.exasol.adapter.dialects.saphana;

import static com.exasol.adapter.metadata.DataType.ExaCharset.ASCII;
import static com.exasol.adapter.metadata.DataType.ExaCharset.UTF8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.BaseIdentifierConverter;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.metadata.DataType;

class HanaColumnMetadataReaderTest {
    private HanaColumnMetadataReader hanaColumnMetadataReader;

    @BeforeEach
    void beforeEach() {
        this.hanaColumnMetadataReader = new HanaColumnMetadataReader(null, AdapterProperties.emptyProperties(),
                BaseIdentifierConverter.createDefault());
    }

    @Test
    void mapVarchar() {
        final JDBCTypeDescription jdbcTypeDescription = new JDBCTypeDescription(12, 0, 5000, 0, "VARCHAR");
        assertThat(this.hanaColumnMetadataReader.mapJdbcType(jdbcTypeDescription),
                equalTo(DataType.createVarChar(5000, ASCII)));
    }

    @Test
    void mapNvarchar() {
        final JDBCTypeDescription jdbcTypeDescription = new JDBCTypeDescription(-9, 0, 5000, 0, "NVARCHAR");
        assertThat(this.hanaColumnMetadataReader.mapJdbcType(jdbcTypeDescription),
                equalTo(DataType.createVarChar(5000, UTF8)));
    }

    @Test
    void mapAlphanum() {
        final JDBCTypeDescription jdbcTypeDescription = new JDBCTypeDescription(-9, 0, 127, 0, "ALPHANUM");
        assertThat(this.hanaColumnMetadataReader.mapJdbcType(jdbcTypeDescription),
                equalTo(DataType.createVarChar(127, ASCII)));
    }

    @Test
    void mapShorttext() {
        final JDBCTypeDescription jdbcTypeDescription = new JDBCTypeDescription(-9, 0, 100, 0, "SHORTTEXT");
        assertThat(this.hanaColumnMetadataReader.mapJdbcType(jdbcTypeDescription),
                equalTo(DataType.createVarChar(100, UTF8)));
    }
}