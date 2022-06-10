package org.openrewrite.java.spring.openapi3;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.Space;
import org.openrewrite.java.utils.annotation.NewAnnotationDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddParameterObjectForPageableArgumentAnnotation extends Recipe {

    private static final NewAnnotationDescriptor REST_CONTROLLER_ANNOTATION = new NewAnnotationDescriptor("org.springframework.web.bind.annotation", "RestController");
    private static final NewAnnotationDescriptor PAGEABLE_DEFAULT_ANNOTATION = new NewAnnotationDescriptor("org.springframework.data.web", "PageableDefault");
    private static final NewAnnotationDescriptor PARAMETER_OBJECT_ANNOTATION = new NewAnnotationDescriptor("org.springdoc.api.annotations", "ParameterObject");
    private static final String PAGEABLE_FULLY_QUALIFIED_NAME = "org.springframework.data.domain.Pageable";

    @Override
    public String getDisplayName() {
        return "Add @ParameterObject annotation to the argument of Pageable type";
    }

    @Override
    protected @Nullable TreeVisitor<?, ExecutionContext> getApplicableTest() {
        return new UsesType<>(REST_CONTROLLER_ANNOTATION.matcher().getAnnotationName());
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {

            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext executionContext) {
                J.MethodDeclaration m = super.visitMethodDeclaration(method, executionContext);

                if (m.getParameters().stream()
                        .filter(p -> p instanceof J.VariableDeclarations)
                        .map(p -> (J.VariableDeclarations) p)
                        .anyMatch(this::hasSuitablePageableArgument)) {

                    maybeAddImport(PARAMETER_OBJECT_ANNOTATION.fullyQualifiedTypeName());

                    m = m.withParameters(m.getParameters().stream()
                            .filter(p -> p instanceof J.VariableDeclarations)
                            .map(p -> (J.VariableDeclarations) p)
                            .map(p -> {
                                final ArrayList<J.Annotation> ans = new ArrayList<>();
                                ans.add(PARAMETER_OBJECT_ANNOTATION.create());

                                if (p.getLeadingAnnotations().size() >= 1) {
                                    final ArrayList<J.Annotation> restAns = new ArrayList<>(p.getLeadingAnnotations());
                                    restAns.set(0, restAns.get(0).withPrefix(Space.build(" ", Collections.emptyList())));
                                    ans.addAll(restAns);
                                }
                                return p.withLeadingAnnotations(ans);
                            })
                            .collect(Collectors.toList()));
                }

                return m;
            }

            private boolean hasSuitablePageableArgument(J.VariableDeclarations p) {
                return p.getLeadingAnnotations().stream()
                        .noneMatch(a -> PARAMETER_OBJECT_ANNOTATION.simpleName().equals(a.getSimpleName()))
                        && p.getLeadingAnnotations().stream()
                        .anyMatch(a -> PAGEABLE_DEFAULT_ANNOTATION.simpleName().equals(a.getSimpleName()))
                        && Optional.ofNullable(p.getTypeAsFullyQualified())
                        .map(fullyQualified -> fullyQualified.isAssignableTo(PAGEABLE_FULLY_QUALIFIED_NAME))
                        .orElse(false);
            }
        };
    }
}
