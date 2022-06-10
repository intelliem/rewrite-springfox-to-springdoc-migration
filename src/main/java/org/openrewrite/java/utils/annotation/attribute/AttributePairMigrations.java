package org.openrewrite.java.utils.annotation.attribute;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AttributePairMigrations {

    private final NavigableMap<LegacyAttribute, AttributePairMigration> pairs;

    public AttributePairMigrations(AttributePairMigration... attributePairMigrations) {
        Objects.requireNonNull(attributePairMigrations);
        this.pairs = Arrays.stream(attributePairMigrations)
                .collect(
                        Collectors.toMap(AttributePairMigration::legacyAttribute,
                                Function.identity(),
                                (m1, m2) -> m1,
                                () -> new TreeMap<>(Comparator.comparing(LegacyAttribute::name))
                        )
                );
    }

    public boolean hasLegacyAttribute(String legacyName) {
        return legacyName != null && pairs.containsKey(new LegacyAttribute(legacyName));
    }

    public NewAttribute getNewAttribute(String legacyName) {
        return pairs.get(new LegacyAttribute(legacyName)).newAttribute();
    }

    public NewAttribute getNewFirstAttribute() {
        return pairs.firstEntry().getValue().newAttribute();
    }

    public AttributePairMigration getAttributePairMigration(String legacyName) {
        return pairs.get(new LegacyAttribute(legacyName));
    }
}
