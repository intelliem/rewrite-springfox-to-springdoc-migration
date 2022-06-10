package org.openrewrite.java.spring.openapi3;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.utils.annotation.LegacyAnnotationDescriptor;
import org.openrewrite.java.utils.annotation.NewAnnotationDescriptor;
import org.openrewrite.java.utils.annotation.attribute.AttributePairMigration;

public class MigrateApiOperationAnnotation extends Recipe {

    private static final LegacyAnnotationDescriptor LEGACY_ANNOTATION =
            new LegacyAnnotationDescriptor("io.swagger.annotations", "ApiOperation");
    private static final NewAnnotationDescriptor NEW_ANNOTATION =
            new NewAnnotationDescriptor("io.swagger.v3.oas.annotations", "Operation",
                    AttributePairMigration.of("value", "summary"));

    @Override
    public String getDisplayName() {
        return "Change @ApiOperation annotation to @Operation annotation";
    }

    @Override
    public String getDescription() {
        return "Changes @ApiOperation annotation to @Operation annotation and applies @ApiOperation annotation attributes to @Operation annotation";
    }

    @Override
    protected @Nullable TreeVisitor<?, ExecutionContext> getApplicableTest() {
        return new UsesType<>(LEGACY_ANNOTATION.matcher().getAnnotationName());
    }

    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new DefaultAnnotationMigrationVisitor(LEGACY_ANNOTATION, NEW_ANNOTATION);
    }
}
