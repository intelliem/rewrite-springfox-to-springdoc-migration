package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class MigrateApiResponseAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val api = """
            package io.swagger.annotations;
            
            public @interface ApiResponse {
                String value();
            }
        """.trimIndent()
    }

    @Test
    fun replaceApiResponseAnnotationToApiResponseAnnotation() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiResponseAnnotation(),
        before = """
                import io.swagger.annotations.ApiResponse;
                
                public class ClassA {
                
                    @ApiResponse(code = "200", message = "Для заявки на создание SAP ID из потребности проекта", response = Number.class)
                    public ResponseEntity<?> someRequestWithResponse() {}
                }
            """,
        after = """
                import io.swagger.v3.oas.annotations.responses.ApiResponse;

                public class ClassA {
                
                    @ApiResponse(responseCode = "200", description = "Для заявки на создание SAP ID из потребности проекта")
                    public ResponseEntity<?> someRequestWithResponse() {}
                }
            """,
        dependsOn = arrayOf(api)
    )
}