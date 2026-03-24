package io.mallang;

import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

public record TestFixture(TestRestTemplate client) {

    public static TestFixture create(Environment environment) {
        TestRestTemplate client = new TestRestTemplate(new RestTemplateBuilder());
        client.setUriTemplateHandler(new LocalHostUriTemplateHandler(environment));
        return new TestFixture(client);
    }

    public void createMemberThenLogin() {
        MemberCreateRequest request = generateCreateRequest();
        client.postForEntity("/members", request, Void.class);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("email", request.email());
        form.add("password", request.password());

        ClientHttpRequestFactory original = client.getRestTemplate().getRequestFactory();
        client.getRestTemplate().setRequestFactory(noRedirectFactory());

        ResponseEntity<Void> loginResponse = client.exchange(
                RequestEntity.post("/login")
                             .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                             .body(form),
                Void.class
        );

        client.getRestTemplate().setRequestFactory(original);

        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        client.getRestTemplate().getInterceptors().add((req, body, execution) -> {
            req.getHeaders().add(COOKIE, sessionCookie);
            return execution.execute(req, body);
        });
    }

    public ResponseEntity<Void> registerShippingAddress(RegisterShippingAddressRequest request) {
        return client.postForEntity("/my/shipping-addresses", request, Void.class);
    }

    public TestRestTemplate unauthenticatedClient() {
        TestRestTemplate unauthenticated = new TestRestTemplate(new RestTemplateBuilder());
        unauthenticated.setUriTemplateHandler(client.getRestTemplate().getUriTemplateHandler());
        unauthenticated.getRestTemplate().setRequestFactory(noRedirectFactory());

        return unauthenticated;
    }

    private ClientHttpRequestFactory noRedirectFactory() {
        return new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setInstanceFollowRedirects(false);
            }
        };
    }
}
