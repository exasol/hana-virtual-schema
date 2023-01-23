package com.exasol.adapter.dialects.saphana.util.dbbuilder;

import com.exasol.db.Identifier;

/**
 * Hana-specific identifier.
 */
public class HanaIdentifier implements Identifier {
    private final String id;

    private HanaIdentifier(final String id) {
        this.id = id;
    }

    /**
     * Create a new {@link HanaIdentifier}.
     *
     * @param id the identifier as {@link String}
     * @return new {@link HanaIdentifier} instance
     */
    public static Identifier of(final String id) {
        return new HanaIdentifier(id);
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public String quote() {
        return "\"" + this.id.replace("\"", "\"\"") + "\"";
    }
}