package io.mallang.annotations;

import io.mallang.security.config.WebMvcConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@CustomDisplayName
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
@Import(WebMvcConfig.class)
public @interface WebMvcAdapterTest {

    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers") Class<?>[] value() default {};
}
