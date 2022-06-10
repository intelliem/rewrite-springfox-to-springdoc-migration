package org.openrewrite.java.utils.annotation;

import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.utils.AnnotationUtils;
import org.openrewrite.java.utils.annotation.attribute.AttributePairMigration;
import org.openrewrite.java.utils.annotation.attribute.AttributePairMigrations;

import java.util.List;
import java.util.Objects;

public class NewAnnotationDescriptor implements AnnotationDescriptor {

    private final String fullyQualifiedTypeName;
    private final String simpleName;

    private final AttributePairMigrations attributePairMigrations;

    public NewAnnotationDescriptor(String packageName, String simpleName, AttributePairMigration attributePairMigration) {

        Objects.requireNonNull(packageName);
        Objects.requireNonNull(simpleName);
        Objects.requireNonNull(attributePairMigration);

        this.simpleName = simpleName;
        this.fullyQualifiedTypeName = packageName + "." + simpleName;
        this.attributePairMigrations = new AttributePairMigrations(attributePairMigration);
    }

    public NewAnnotationDescriptor(String packageName, String simpleName, AttributePairMigration... attributePairMigrations) {

        Objects.requireNonNull(packageName);
        Objects.requireNonNull(simpleName);
        Objects.requireNonNull(attributePairMigrations);

        this.simpleName = simpleName;
        this.fullyQualifiedTypeName = packageName + "." + simpleName;
        this.attributePairMigrations = new AttributePairMigrations(attributePairMigrations);
    }

    @Override
    public String simpleName() {
        return simpleName;
    }

    @Override
    public String fullyQualifiedTypeName() {
        return fullyQualifiedTypeName;
    }

    public J.Annotation replace(J.Annotation a) {
        final J.Identifier oldIdentifier = (J.Identifier) a.getAnnotationType();
        final J.Identifier newIdentifier = oldIdentifier.withSimpleName(simpleName())
                .withType(JavaType.buildType(fullyQualifiedTypeName()));

        final List<Expression> newAttributes = AnnotationUtils.buildNewAttributeExpressions(a.getArguments(), attributePairMigrations);

        if (newAttributes.isEmpty()) {
            return a.withAnnotationType(newIdentifier);
        }

        return a.withAnnotationType(newIdentifier)
                .withArguments(newAttributes);
    }
}
