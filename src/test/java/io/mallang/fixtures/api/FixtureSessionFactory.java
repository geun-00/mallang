package io.mallang.fixtures.api;

import org.springframework.core.env.Environment;

public final class FixtureSessionFactory {

    private final Environment environment;

    public FixtureSessionFactory(Environment environment) {
        this.environment = environment;
    }

    public FixtureSession create() {
        return new FixtureSession(new FixtureContext(environment));
    }
}
