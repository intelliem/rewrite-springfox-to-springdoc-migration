package org.openrewrite.java.utils.annotation.attribute;

public record AttributePairMigration(LegacyAttribute legacyAttribute, NewAttribute newAttribute) {

    public static AttributePairMigration of(String legacyName, String newName) {
        return new AttributePairMigration(new LegacyAttribute(legacyName), new NewAttribute(newName));
    }
}
