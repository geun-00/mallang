package io.mallang.fixtures.api;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

final class FixtureContext {

    private final TestRestTemplate authenticatedClient;
    private final TestRestTemplate unauthenticatedClient;

    FixtureContext(Environment environment) {
        this.authenticatedClient = newClient(environment, false);
        this.unauthenticatedClient = newClient(environment, true);
    }

    public TestRestTemplate authenticatedClient() {
        return authenticatedClient;
    }

    public TestRestTemplate unauthenticatedClient() {
        return unauthenticatedClient;
    }

    private TestRestTemplate newClient(Environment environment, boolean disableRedirect) {
        TestRestTemplate client = new TestRestTemplate(new RestTemplateBuilder());
        client.setUriTemplateHandler(new LocalHostUriTemplateHandler(environment));

//        if (disableRedirect) {
//            client.getRestTemplate().setRequestFactory(noRedirectFactory());
//        }

        return client;
    }

    private ClientHttpRequestFactory noRedirectFactory() {
        var httpClient = HttpClientBuilder.create()
                                          .disableRedirectHandling()
                                          .build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
