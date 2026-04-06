package io.mallang;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

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

    protected String extractId(ResponseEntity<Void> response) {
        String path = response.getHeaders().getLocation().getPath();

        return path.substring(path.lastIndexOf('/') + 1);
    }

    public TestRestTemplate client() {
        return authenticatedClient();
    }

    public TestRestTemplate unauthenticatedClient() {
        return context.unauthenticatedClient();
    }
}
