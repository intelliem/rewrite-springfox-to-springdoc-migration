package org.openrewrite.java.utils.annotation;

import org.openrewrite.java.AnnotationMatcher;

import java.util.Objects;

public class LegacyAnnotationDescriptor implements AnnotationDescriptor {

    private final String fullyQualifiedTypeName;
    private final String simpleName;
    private final AnnotationMatcher matcher;

    public LegacyAnnotationDescriptor(String packageName, String simpleName) {
        Objects.requireNonNull(packageName);
        Objects.requireNonNull(simpleName);

        this.simpleName = simpleName;
        this.fullyQualifiedTypeName = packageName + "." + simpleName;
        this.matcher = new AnnotationMatcher("@" + this.fullyQualifiedTypeName);
    }

    @Override
    public String simpleName() {
        return simpleName;
    }

    @Override
    public String fullyQualifiedTypeName() {
        return fullyQualifiedTypeName;
    }

    public AnnotationMatcher matcher() {
        return matcher;
    }
}
