package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class RemoveApiIgnoreAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val apiIgnore = """
            package springfox.documentation.annotations;
            
            public @interface ApiIgnore {
                String value();
            }
        """.trimIndent()

        private val apiResponse = """
            package io.swagger.annotations;
            
            public @interface ApiResponse {
                String value();
            }
        """.trimIndent()
    }

    @Test
    fun removeApiIgnoreAnnotationInMethodAnnotations() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = RemoveApiIgnoreAnnotation().doNext(MigrateApiResponseAnnotation()),
        before = """
                import io.swagger.annotations.ApiResponse;
                import springfox.documentation.annotations.ApiIgnore;
                
                public class ClassA {
                
                    @ApiIgnore @ApiResponse(code = "200", message = "Для заявки на создание SAP ID из потребности проекта", response = Number.class)
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
        dependsOn = arrayOf(apiIgnore, apiResponse)
    )

    @Test
    fun removeApiIgnoreAnnotationInMethodParameters() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = RemoveApiIgnoreAnnotation(),
        before = """
                import springfox.documentation.annotations.ApiIgnore;
                
                public class ClassA {
                    
                    public ResponseEntity<?> someRequestWithResponse(@ApiIgnore Integer doNotShow) {}
                }
            """,
        after = """
                public class ClassA {
                    
                    public ResponseEntity<?> someRequestWithResponse( Integer doNotShow) {}
                }
            """,
        dependsOn = arrayOf(apiIgnore, apiResponse)
    )
}