package org.openrewrite.java.utils.annotation;

import org.openrewrite.java.AnnotationMatcher;

public interface AnnotationDescriptor {

    String simpleName();

    String fullyQualifiedTypeName();
}
