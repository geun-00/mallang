package io.mallang;

import io.mallang.fixtures.api.FixtureSession;
import io.mallang.fixtures.api.FixtureSessionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@TestConfiguration
public class TestFixtureConfiguration {

    @Bean
    public FixtureSessionFactory fixtureSessionFactory(Environment environment) {
        return new FixtureSessionFactory(environment);
    }

    @Bean
    @Scope("prototype")
    public FixtureSession fixtureSession(FixtureSessionFactory fixtureSessionFactory) {
        return fixtureSessionFactory.create();
    }
}
