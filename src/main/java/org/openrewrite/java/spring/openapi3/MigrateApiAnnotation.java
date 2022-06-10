package org.openrewrite.java.spring.openapi3;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.utils.annotation.LegacyAnnotationDescriptor;
import org.openrewrite.java.utils.annotation.NewAnnotationDescriptor;
import org.openrewrite.java.utils.annotation.attribute.AttributePairMigration;

public class MigrateApiAnnotation extends Recipe {

    private static final LegacyAnnotationDescriptor LEGACY_ANNOTATION =
            new LegacyAnnotationDescriptor("io.swagger.annotations", "Api");
    private static final NewAnnotationDescriptor NEW_ANNOTATION =
            new NewAnnotationDescriptor("io.swagger.v3.oas.annotations.tags", "Tag",
                    AttributePairMigration.of("tags", "name"));

    @Override
    public String getDisplayName() {
        return "Change @Api annotation to @Tag annotation";
    }

    @Override
    public String getDescription() {
        return "Changes @Api annotation to @Tag annotation and applies @Api annotation attributes to @Tag annotation";
    }

    @Override
    protected @Nullable TreeVisitor<?, ExecutionContext> getApplicableTest() {
        return new UsesType<>(LEGACY_ANNOTATION.matcher().getAnnotationName());
    }

    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {

            @Override
            public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext executionContext) {
                J.Annotation a = super.visitAnnotation(annotation, executionContext);

                if (LEGACY_ANNOTATION.matcher().matches(annotation)) {

                    maybeAddImport(NEW_ANNOTATION.fullyQualifiedTypeName());
                    maybeRemoveImport(LEGACY_ANNOTATION.fullyQualifiedTypeName());

                    return NEW_ANNOTATION.replace(a);
                }

                return a;
            }
        };
    }
}
