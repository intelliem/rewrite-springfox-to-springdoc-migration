package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class MigrateApiModelAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val api = """
            package io.swagger.annotations;
            
            public @interface ApiModel {
                String description() default "";
            }
        """.trimIndent()
    }

    @Test
    fun replaceApiModelAnnotationToSchemaAnnotation() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiModelAnnotation(),
        before = """
                import io.swagger.annotations.ApiModel;
                
                @ApiModel(description = "Test Model")
                public class Model {}
            """,
        after = """
                import io.swagger.v3.oas.annotations.media.Schema;
                
                @Schema(description = "Test Model")
                public class Model {}
            """,
        dependsOn = arrayOf(api)
    )
}