package org.openrewrite.java.spring.openapi3

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.java.JavaRecipeTest
import org.openrewrite.test.RewriteTest

class AddParameterObjectForPageableArgumentAnnotationTest : JavaRecipeTest, RewriteTest {

    companion object {
        private val restController = """
            package org.springframework.web.bind.annotation;
            
            public @interface RestController {
                String value() default "";
            }
        """.trimIndent()

        private val pageable = """
            package org.springframework.data.domain;
            
            public interface Pageable {
                default boolean isPaged() {
		            return true;
	            }
                
                int getPageNumber();
                
                int getPageSize();
                
                long getOffset();
            }
        """.trimIndent()

        private val parameterObject = """
            package org.springdoc.api.annotations;
            
            public @interface ParameterObject {
            }
        """.trimIndent()

        private val pageableDefault = """
            package org.springframework.data.web;
            
            public @interface PageableDefault {
                int value() default 10;

                int size() default 10;

                int page() default 0;
            }
        """.trimIndent()
    }

    @Test
    fun addParameterObjectAnnotationToPageableArgument() = assertChanged(
        parser = JavaParser.fromJavaVersion()
            .logCompilationWarningsAndErrors(true)
            .classpath("guava")
            .build(),
        recipe = AddParameterObjectForPageableArgumentAnnotation(),
        before = """
                import org.springframework.web.bind.annotation.RestController;
                import org.springframework.data.domain.Pageable;
                import org.springframework.data.web.PageableDefault;
                
                @RestController
                public class Controller {
                    
                    public ResponseEntity<?> someRequestWithResponse(@PageableDefault(sort = "id", size = 100) Pageable pageable) {}
                }
            """,
        after = """
                import org.springframework.web.bind.annotation.RestController;
                import org.springdoc.api.annotations.ParameterObject;
                import org.springframework.data.domain.Pageable;
                import org.springframework.data.web.PageableDefault;
                
                @RestController
                public class Controller {
                    
                    public ResponseEntity<?> someRequestWithResponse(@ParameterObject @PageableDefault(sort = "id", size = 100) Pageable pageable) {}
                }
            """,
        dependsOn = arrayOf(pageable, restController, parameterObject, pageableDefault)
    )
}