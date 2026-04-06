package io.mallang;

import org.springframework.boot.test.web.client.TestRestTemplate;

public abstract class ApiFixture {

    private final FixtureContext context;

    protected ApiFixture(FixtureContext context) {
        this.context = context;
    }

    protected TestRestTemplate authenticatedClient() {
        return context.authenticatedClient();
    }

    protected FixtureContext context() {
        return context;
    }

    public TestRestTemplate client() {
        return authenticatedClient();
    }

    public TestRestTemplate unauthenticatedClient() {
        return context.unauthenticatedClient();
    }
}
