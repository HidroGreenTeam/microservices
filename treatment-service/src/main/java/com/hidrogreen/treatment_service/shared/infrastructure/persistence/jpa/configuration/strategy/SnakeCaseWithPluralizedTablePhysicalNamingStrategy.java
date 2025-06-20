package com.hidrogreen.treatment_service.shared.infrastructure.persistence.jpa.configuration.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Snake Case With Pluralized Table Physical Naming Strategy
 * @summary
 * PhysicalNamingStrategy implementation that converts entity names to snake_case and table names to pluralized snake_case.
 *
 * @since 1.0.0
 */
public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {

    /**
     * Converts the physical catalog name to snake_case.
     * @param identifier The catalog name.
     * @param jdbcEnvironment The JDBC environment.
     * @return The snake_case catalog name.
     */
    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the physical schema name to snake_case.
     * @param identifier The schema name.
     * @param jdbcEnvironment The JDBC environment.
     * @return The snake_case schema name.
     */
    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the physical table name to snake_case and pluralizes it.
     * @param identifier The table name.
     * @param jdbcEnvironment The JDBC environment.
     * @return The snake_case and pluralized table name.
     */
    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(this.toPlural(identifier));
    }

    /**
     * Converts the physical sequence name to snake_case.
     * @param identifier The sequence name.
     * @param jdbcEnvironment The JDBC environment.
     * @return The snake_case sequence name.
     */
    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the physical column name to snake_case.
     * @param identifier The column name.
     * @param jdbcEnvironment The JDBC environment.
     * @return The snake_case column name.
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the name of an Identifier to snake_case.
     * @param identifier The Identifier to convert.
     * @return The snake_case Identifier.
     */
    private Identifier toSnakeCase(final Identifier identifier) {
        if (identifier == null) return null;
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText().replaceAll(regex, replacement).toLowerCase();
        return Identifier.toIdentifier(newName);
    }

    /**
     * Converts the name of an Identifier to plural form.
     * Simple pluralization for English words (covers most common cases)
     * @param identifier The Identifier to convert.
     * @return The pluralized Identifier.
     */
    private Identifier toPlural(final Identifier identifier) {
        if (identifier == null) return null;
        final String newName = pluralize(identifier.getText());
        return Identifier.toIdentifier(newName);
    }

    /**
     * Simple pluralization function for English words
     * Covers the most common pluralization rules
     */
    private String pluralize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        String lowerWord = word.toLowerCase();

        // Words ending in 's', 'ss', 'sh', 'ch', 'x', 'z' -> add 'es'
        if (lowerWord.endsWith("s") || lowerWord.endsWith("ss") || 
            lowerWord.endsWith("sh") || lowerWord.endsWith("ch") || 
            lowerWord.endsWith("x") || lowerWord.endsWith("z")) {
            return word + "es";
        }

        // Words ending in 'y' preceded by consonant -> change 'y' to 'ies'
        if (lowerWord.endsWith("y") && lowerWord.length() > 1) {
            char beforeY = lowerWord.charAt(lowerWord.length() - 2);
            if (!"aeiou".contains(String.valueOf(beforeY))) {
                return word.substring(0, word.length() - 1) + "ies";
            }
        }

        // Words ending in 'f' or 'fe' -> change to 'ves'
        if (lowerWord.endsWith("f")) {
            return word.substring(0, word.length() - 1) + "ves";
        }
        if (lowerWord.endsWith("fe")) {
            return word.substring(0, word.length() - 2) + "ves";
        }

        // Default: just add 's'
        return word + "s";
    }
}
