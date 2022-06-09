package org.openrewrite.java.utils;

import org.openrewrite.java.tree.J;

import java.util.Objects;
import java.util.Optional;

public class AnnotationUtils {

    public static Optional<String> extractArgumentValueFrom(J.Annotation annotation, String attributeName) {
        if (annotation.getArguments() == null || annotation.getArguments().isEmpty()) {
            return Optional.empty();
        }

        return annotation.getArguments()
                .stream()
                .map(e -> {
                    if (e instanceof J.Assignment assignment) {
                        if (assignment.getVariable() instanceof J.Identifier identifier) {
                            if (identifier.getSimpleName().equals(attributeName)) {
                                return ((J.Literal) assignment.getAssignment()).getValueSource();
                            } else {
                                return null;
                            }
                        }
                    } else if (e instanceof J.Literal literal) {
                        return literal.getValueSource();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst();
    }
}
