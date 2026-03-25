package io.mallang;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@TestConfiguration
public class TestFixtureConfiguration {

    @Bean
    @Scope("prototype")
    public TestFixture testFixture(Environment environment) {
        return TestFixture.create(environment);
    }
}
