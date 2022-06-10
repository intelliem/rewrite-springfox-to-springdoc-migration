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
    }

    @Test
    fun replaceApiAnnotationToTagAnnotationWhenNoValueIsUsed() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiAnnotation(),
        before = """
                import io.swagger.annotations.Api;
                
                @Api
                public class TestApiController {}
            """,
        after = """
                import io.swagger.v3.oas.annotations.tags.Tag;
                
                @Tag
                public class TestApiController {}
            """,
        dependsOn = arrayOf(api)
    )

    @Test
    fun replaceApiAnnotationToTagAnnotation() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiAnnotation(),
        before = """
                import io.swagger.annotations.Api;
                
                @Api(tags = "Test API")
                public class TestApiController {}
            """,
        after = """
                import io.swagger.v3.oas.annotations.tags.Tag;
                
                @Tag(name = "Test API")
                public class TestApiController {}
            """,
        dependsOn = arrayOf(api)
    )

    @Test
    fun replaceApiAnnotationToTagAnnotationWhenDefaultValueIsUsed() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = MigrateApiAnnotation(),
        before = """
                import io.swagger.annotations.Api;
                
                @Api("Test API")
                public class TestApiController {}
            """,
        after = """
                import io.swagger.v3.oas.annotations.tags.Tag;
                
                @Tag(name = "Test API")
                public class TestApiController {}
            """,
        dependsOn = arrayOf(api)
    )
}