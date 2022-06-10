package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class MigrateApiModelPropertyAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val api = """
            package io.swagger.annotations;
            
            public @interface ApiModelProperty {
                String description() default "";
                
                boolean required() default false;
            }
        """.trimIndent()
    }

    @Test
    fun replaceApiModelPropertyAnnotationToSchemaAnnotationWhenOneAttributeIsUsed() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiModelPropertyAnnotation(),
        before = """
                import io.swagger.annotations.ApiModelProperty;
                
                @ApiModelProperty("Test Model Property1")
                public class Model {}
            """,
        after = """
                import io.swagger.v3.oas.annotations.media.Schema;
                
                @Schema(description = "Test Model Property1")
                public class Model {}
            """,
        dependsOn = arrayOf(api)
    )

    @Test
    fun replaceApiModelPropertyAnnotationToSchemaAnnotationWhenSeveralAttributesIsUsed() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiModelPropertyAnnotation(),
        before = """
                import io.swagger.annotations.ApiModelProperty;
                
                @ApiModelProperty(value = "Test Model Property1", required = true)
                public class Model {}
            """,
        after = """
                import io.swagger.v3.oas.annotations.media.Schema;
                
                @Schema(description = "Test Model Property1", required = true)
                public class Model {}
            """,
        dependsOn = arrayOf(api)
    )
}