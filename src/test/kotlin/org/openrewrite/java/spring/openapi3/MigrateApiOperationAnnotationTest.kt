package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class MigrateApiOperationAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val api = """
            package io.swagger.annotations;
            
            public @interface ApiOperation {
                String value();
            }
        """.trimIndent()
    }

    @Test
    fun replaceApiOperationAnnotationToOperationAnnotation() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiOperationAnnotation(),
        before = """
                import io.swagger.annotations.ApiOperation;
                
                public class ClassA {
                
                    @ApiOperation("Request 1")
                    public void someRequest() {}
                }
            """,
        after = """
                import io.swagger.v3.oas.annotations.Operation;

                public class ClassA {
                
                    @Operation(summary = "Request 1")
                    public void someRequest() {}
                }
            """,
        dependsOn = arrayOf(api)
    )

    @Test
    fun replaceApiOperationAnnotationToOperationAnnotationWhenValueIsUsed() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiOperationAnnotation(),
        before = """
                import io.swagger.annotations.ApiOperation;
                
                public class ClassA {
                
                    @ApiOperation(value = "Request 1")
                    public void someRequest() {}
                }
            """,
        after = """
                import io.swagger.v3.oas.annotations.Operation;

                public class ClassA {
                
                    @Operation(summary = "Request 1")
                    public void someRequest() {}
                }
            """,
        dependsOn = arrayOf(api)
    )
}