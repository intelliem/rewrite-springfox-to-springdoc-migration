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

public class MigrateApiModelPropertyAnnotation extends Recipe {

    private static final LegacyAnnotationDescriptor LEGACY_ANNOTATION =
            new LegacyAnnotationDescriptor("io.swagger.annotations", "ApiModelProperty");
    private static final NewAnnotationDescriptor NEW_ANNOTATION =
            new NewAnnotationDescriptor("io.swagger.v3.oas.annotations.media", "Schema",
                    AttributePairMigration.of("value", "description"),
                    AttributePairMigration.of("required", "required"),
                    AttributePairMigration.of("example", "example")
            );

    @Override
    public String getDisplayName() {
        return "Change @ApiModelProperty annotation to @Schema annotation";
    }

    @Override
    public String getDescription() {
        return "Changes @ApiModelProperty annotation to @Schema annotation and applies @ApiModelProperty annotation attributes to @Schema annotation";
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
