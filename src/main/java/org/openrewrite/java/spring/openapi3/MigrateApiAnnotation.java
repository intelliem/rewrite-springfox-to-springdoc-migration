package org.openrewrite.java.spring.openapi3;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.utils.AnnotationUtils;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;

import java.util.Optional;

public class MigrateApiAnnotation extends Recipe {

    private static final AnnotationMatcher API_ANNOTATION_MATCHER = new AnnotationMatcher("@io.swagger.annotations.Api");
    private static final String API_ANNOTATION_TYPE_NAME = "io.swagger.annotations.Api";

    private static final String TAG_ANNOTATION_TYPE_NAME = "io.swagger.v3.oas.annotations.tags.Tag";
    private static final String TAG_ANNOTATION_SIMPLE_NAME = "Tag";

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
        return new UsesType<>(API_ANNOTATION_MATCHER.getAnnotationName());
    }

    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {

            private final JavaTemplate tagNameAttribute = JavaTemplate.builder(this::getCursor, "name = #{}")
                    .imports(TAG_ANNOTATION_TYPE_NAME)
                    .build();

            @Override
            public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext executionContext) {
                J.Annotation a = super.visitAnnotation(annotation, executionContext);

                if (API_ANNOTATION_MATCHER.matches(annotation)) {
                    final Optional<String> value = AnnotationUtils.extractArgumentValueFrom(a, "tags");

                    final J.Identifier oldIdentifier = (J.Identifier) a.getAnnotationType();
                    final J.Identifier newIdentifier = oldIdentifier.withSimpleName(TAG_ANNOTATION_SIMPLE_NAME)
                            .withType(JavaType.buildType(TAG_ANNOTATION_TYPE_NAME));

                    a = a.withAnnotationType(newIdentifier)
                            .withTemplate(tagNameAttribute, a.getCoordinates().replaceArguments(), value.get());

                    maybeAddImport(TAG_ANNOTATION_TYPE_NAME);
                    maybeRemoveImport(API_ANNOTATION_TYPE_NAME);
                }

                return a;
            }
        };
    }
}
