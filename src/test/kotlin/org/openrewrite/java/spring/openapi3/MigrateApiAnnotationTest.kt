package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class MigrateApiAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val api = """
            package io.swagger.annotations;
            
            public @interface Api {
                String value() default "";
                String[] tags() default "";
            }
        """.trimIndent()

        private val tag = """
            package io.swagger.v3.oas.annotations.tags;
            
            public @interface Tag {
                String name();
                String description() default "";
            }
        """.trimIndent()
    }

    @Test
    fun replaceApiAnnotationToTagAnnotation() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiAnnotation(),
        dependsOn = arrayOf(tag, api),
        before = """
                import io.swagger.annotations.Api;
                
                @Api(tags = "Test API")
                public class TestApiController {}
            """,
        after = """
                import io.swagger.v3.oas.annotations.tags.Tag;
                
                @Tag(name = "Test API")
                public class TestApiController {}
            """
    )
}