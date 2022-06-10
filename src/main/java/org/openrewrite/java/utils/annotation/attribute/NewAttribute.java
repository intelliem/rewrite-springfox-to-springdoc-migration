package org.openrewrite.java.utils.annotation.attribute;

import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JLeftPadded;
import org.openrewrite.java.tree.Space;
import org.openrewrite.marker.Markers;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public record NewAttribute(String name) {

    public NewAttribute {
        Objects.requireNonNull(name);
    }

    public Expression buildAttributeExpression(J.Assignment assignment, J.Identifier identifier, J.Literal literal) {
        final String newValueSource = literal.getValueSource();

        return assignment.withVariable(identifier.withSimpleName(name()))
                .withAssignment(literal.withValueSource(newValueSource));
    }

    public boolean isValueAttribute() {
        return "value".equals(name);
    }

    public Expression buildAttributeExpression(J.Literal literal) {
        final String newValueSource = literal.getValueSource();
        final String newSimpleName = name();

        final J.Identifier identifier = new J.Identifier(UUID.randomUUID(), Space.EMPTY, Markers.EMPTY, newSimpleName, literal.getType(), null);

        final JLeftPadded<Expression> innerAssignment = new JLeftPadded<>(
                Space.build(" ", Collections.emptyList()),
                literal.withValueSource(newValueSource).withPrefix(Space.build(" ", Collections.emptyList())),
                Markers.EMPTY);

        return new J.Assignment(UUID.randomUUID(), Space.EMPTY, Markers.EMPTY, identifier, innerAssignment, literal.getType());
    }
}
