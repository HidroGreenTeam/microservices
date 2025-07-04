package com.hidrogreen.treatment_service.shared.infrastructure.persistence.jpa.configuration.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;


public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {

    
    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    
    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    
    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(this.toPlural(identifier));
    }

    
    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    
    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    
    private Identifier toSnakeCase(final Identifier identifier) {
        if (identifier == null) return null;
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText().replaceAll(regex, replacement).toLowerCase();
        return Identifier.toIdentifier(newName);
    }

    
    private Identifier toPlural(final Identifier identifier) {
        if (identifier == null) return null;
        final String newName = pluralize(identifier.getText());
        return Identifier.toIdentifier(newName);
    }

    
    private String pluralize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        String lowerWord = word.toLowerCase();

        
        if (lowerWord.endsWith("s") || lowerWord.endsWith("ss") || 
            lowerWord.endsWith("sh") || lowerWord.endsWith("ch") || 
            lowerWord.endsWith("x") || lowerWord.endsWith("z")) {
            return word + "es";
        }

        
        if (lowerWord.endsWith("y") && lowerWord.length() > 1) {
            char beforeY = lowerWord.charAt(lowerWord.length() - 2);
            if (!"aeiou".contains(String.valueOf(beforeY))) {
                return word.substring(0, word.length() - 1) + "ies";
            }
        }

        
        if (lowerWord.endsWith("f")) {
            return word.substring(0, word.length() - 1) + "ves";
        }
        if (lowerWord.endsWith("fe")) {
            return word.substring(0, word.length() - 2) + "ves";
        }

        
        return word + "s";
    }
}
