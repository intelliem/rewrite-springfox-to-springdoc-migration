package org.openrewrite.java.utils;

import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.utils.annotation.attribute.AttributePairMigration;
import org.openrewrite.java.utils.annotation.attribute.AttributePairMigrations;
import org.openrewrite.java.utils.annotation.attribute.LegacyAttribute;
import org.openrewrite.java.utils.annotation.attribute.NewAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AnnotationUtils {

    private static Optional<Expression> buildAttributeExpression(Expression argument, AttributePairMigrations attributePairMigrations) {
        if (argument instanceof J.Assignment assignment
                && assignment.getVariable() instanceof J.Identifier identifier
                && assignment.getAssignment() instanceof J.Literal literal
                && attributePairMigrations.hasLegacyAttribute(identifier.getSimpleName())) {

            final NewAttribute newAttribute = attributePairMigrations.getNewAttribute(identifier.getSimpleName());
            return Optional.of(newAttribute.buildAttributeExpression(assignment, identifier, literal));

        } else if (argument instanceof J.Literal literal) {
            if (attributePairMigrations.hasLegacyAttribute("value")) {
                final AttributePairMigration attributePairMigration = attributePairMigrations.getAttributePairMigration("value");
                final NewAttribute newAttribute = attributePairMigration.newAttribute();
                final LegacyAttribute legacyAttribute = attributePairMigration.legacyAttribute();

                if (legacyAttribute.isValueAttribute() && newAttribute.isValueAttribute()) {
                    return Optional.of(literal);
                } else {
                    return Optional.of(newAttribute.buildAttributeExpression(literal));
                }
            } else {
                final NewAttribute newAttribute = attributePairMigrations.getNewFirstAttribute();
                return Optional.of(newAttribute.buildAttributeExpression(literal));
            }
        } else {
            return Optional.empty();
        }
    }

    public static List<Expression> buildNewAttributeExpressions(List<Expression> arguments, AttributePairMigrations attributePairMigrations) {
        if (arguments == null || arguments.isEmpty()) {
            return Collections.emptyList();
        }

        if (arguments.size() == 1) {
            return buildAttributeExpression(arguments.get(0), attributePairMigrations)
                    .map(List::of)
                    .orElseGet(Collections::emptyList);
        }

        return arguments.stream()
                .map(e -> {
                    if (e instanceof J.Assignment assignment
                            && assignment.getVariable() instanceof J.Identifier identifier
                            && assignment.getAssignment() instanceof J.Literal literal
                            && attributePairMigrations.hasLegacyAttribute(identifier.getSimpleName())) {

                        final NewAttribute newAttribute = attributePairMigrations.getNewAttribute(identifier.getSimpleName());

                        return newAttribute.buildAttributeExpression(assignment, identifier, literal);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
