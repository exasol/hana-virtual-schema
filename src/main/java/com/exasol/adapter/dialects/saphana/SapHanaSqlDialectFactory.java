package com.exasol.adapter.dialects.saphana;

import com.exasol.adapter.dialects.*;
import com.exasol.logging.VersionCollector;

/**
 * Factory for the HANA dialect.
 */
public class SapHanaSqlDialectFactory implements SqlDialectFactory {
    @Override
    public String getSqlDialectName() {
        return SapHanaSqlDialect.NAME;
    }

    @Override
    public SqlDialect createSqlDialect(final JDBCAdapterContext context) {
        return new SapHanaSqlDialect(context);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/hana-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }

    @Override
    public String getAdapterProjectShortTag() {
        return "VSHANA";
    }
}
