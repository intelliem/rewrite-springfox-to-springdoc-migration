package org.openrewrite.java.spring.openapi3;

import org.openrewrite.ExecutionContext;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.utils.annotation.LegacyAnnotationDescriptor;
import org.openrewrite.java.utils.annotation.NewAnnotationDescriptor;

class DefaultAnnotationMigrationVisitor extends JavaIsoVisitor<ExecutionContext> {

    private final LegacyAnnotationDescriptor legacyAnnotation;
    private final NewAnnotationDescriptor newAnnotation;

    public DefaultAnnotationMigrationVisitor(LegacyAnnotationDescriptor legacyAnnotation, NewAnnotationDescriptor newAnnotation) {
        this.legacyAnnotation = legacyAnnotation;
        this.newAnnotation = newAnnotation;
    }

    @Override
    public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext executionContext) {
        J.Annotation a = super.visitAnnotation(annotation, executionContext);

        if (legacyAnnotation.matcher().matches(annotation)) {

            maybeAddImport(newAnnotation.fullyQualifiedTypeName());
            maybeRemoveImport(legacyAnnotation.fullyQualifiedTypeName());

            return newAnnotation.replace(a);
        }

        return a;
    }
}
