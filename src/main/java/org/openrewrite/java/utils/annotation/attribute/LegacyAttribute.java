package org.openrewrite.java.utils.annotation.attribute;

import java.util.Objects;

public record LegacyAttribute(String name) {
    public LegacyAttribute {
        Objects.requireNonNull(name);
    }
    public boolean isValueAttribute() {
        return "value".equals(name);
    }
}
