package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class MigrateApiParamAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val api = """
            package io.swagger.annotations;
            
            public @interface ApiParam {
                String name() default "";
                String value() default "";
                String defaultValue() default "";
                boolean hidden() default false;
            }
        """.trimIndent()
    }

    @Test
    fun replaceApiParamAnnotationToParameterAnnotation() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiParamAnnotation(),
        before = """
                import io.swagger.annotations.ApiParam;
                
                public class ClassA {
                    
                    public ResponseEntity<?> someRequest(@ApiParam(name = "page", type = "integer", defaultValue = "0", hidden = true) Long page) {}
                }
            """,
        after = """
                import io.swagger.v3.oas.annotations.Parameter;

                public class ClassA {
                    
                    public ResponseEntity<?> someRequest(@Parameter(name = "page", example = "0", hidden = true) Long page) {}
                }
            """,
        dependsOn = arrayOf(api)
    )
}