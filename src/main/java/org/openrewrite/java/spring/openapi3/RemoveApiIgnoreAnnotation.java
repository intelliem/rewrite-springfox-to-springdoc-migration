package org.openrewrite.java.spring.openapi3;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.java.utils.annotation.LegacyAnnotationDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveApiIgnoreAnnotation extends Recipe {

    private static final LegacyAnnotationDescriptor LEGACY_ANNOTATION =
            new LegacyAnnotationDescriptor("springfox.documentation.annotations", "ApiIgnore");

    @Override
    public String getDisplayName() {
        return "Remove @ApiIgnore annotation";
    }

    @Override
    protected @Nullable TreeVisitor<?, ExecutionContext> getApplicableTest() {
        return new UsesType<>(LEGACY_ANNOTATION.matcher().getAnnotationName());
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {

            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext executionContext) {
                J.MethodDeclaration m = super.visitMethodDeclaration(method, executionContext);

                if (m.getLeadingAnnotations().stream().anyMatch(this::isLegacyAnnotation)) {
                    m = m.withLeadingAnnotations(removeLegacyAnnotations(m.getLeadingAnnotations()));
                }

                if (m.getParameters().stream()
                        .filter(p -> p instanceof J.VariableDeclarations)
                        .map(p -> (J.VariableDeclarations) p)
                        .map(J.VariableDeclarations::getAllAnnotations)
                        .flatMap(Collection::stream)
                        .anyMatch(this::isLegacyAnnotation)) {


                    m = m.withParameters(m.getParameters().stream()
                            .filter(p -> p instanceof J.VariableDeclarations)
                            .map(p -> (J.VariableDeclarations) p)
                            .map(p -> {
                                if (p.getLeadingAnnotations().stream().anyMatch(this::isLegacyAnnotation)) {
                                    return p.withLeadingAnnotations(removeLegacyAnnotations(p.getLeadingAnnotations()));
                                } else {
                                    return p;
                                }
                            })
                            .collect(Collectors.toList()));
                }

                return m;
            }


            private List<J.Annotation> removeLegacyAnnotations(List<J.Annotation> m) {
                return ListUtils.map(m, anno -> {
                    if (isLegacyAnnotation(anno)) {
                        maybeRemoveImport(LEGACY_ANNOTATION.fullyQualifiedTypeName());
                        return null;
                    }
                    return anno;
                });
            }

            private boolean isLegacyAnnotation(J.Annotation a) {
                return TypeUtils.isOfClassType(a.getType(), LEGACY_ANNOTATION.fullyQualifiedTypeName());
            }
        };
    }
}
