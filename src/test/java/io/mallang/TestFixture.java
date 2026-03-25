package io.mallang;

import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.MemberFixture.generateRegisterShippingAddressRequest;
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
        registerMember(request);

        String initialCookie = fetchInitialCookieWithCsrfToken();
        String sessionCookie = loginWithCsrf(request, initialCookie);
        String newCsrfToken = fetchCsrfTokenFromNewSession(sessionCookie);
        registerAuthInterceptor(sessionCookie, newCsrfToken);
    }

    private String fetchInitialCookieWithCsrfToken() {
        ResponseEntity<String> loginPage = client.getForEntity("/login", String.class);
        return loginPage.getHeaders().getFirst(SET_COOKIE);
    }

    private String loginWithCsrf(MemberCreateRequest request, String initialCookie) {
        String csrfToken = fetchCsrfTokenFromCookie(initialCookie);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("email", request.email());
        form.add("password", request.password());
        form.add("_csrf", csrfToken);

        ClientHttpRequestFactory original = client.getRestTemplate().getRequestFactory();
        client.getRestTemplate().setRequestFactory(noRedirectFactory());

        ResponseEntity<Void> loginResponse = client.exchange(
                RequestEntity.post("/login")
                             .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                             .header(COOKIE, initialCookie)
                             .body(form),
                Void.class
        );

        client.getRestTemplate().setRequestFactory(original);

        return loginResponse.getHeaders().getFirst(SET_COOKIE);
    }

    private String fetchCsrfTokenFromNewSession(String sessionCookie) {
        ResponseEntity<String> afterLoginPage = client.exchange(
                RequestEntity.get("/login")
                             .header(COOKIE, sessionCookie)
                             .build(),
                String.class
        );
        return extractCsrfToken(afterLoginPage.getBody());
    }

    private void registerAuthInterceptor(String sessionCookie, String csrfToken) {
        client.getRestTemplate().getInterceptors().add((req, body, execution) -> {
            req.getHeaders().add(COOKIE, sessionCookie);
            req.getHeaders().add("X-CSRF-TOKEN", csrfToken);
            return execution.execute(req, body);
        });
    }

    private String fetchCsrfTokenFromCookie(String initialCookie) {
        ResponseEntity<String> loginPage = client.exchange(
                RequestEntity.get("/login")
                             .header(COOKIE, initialCookie)
                             .build(),
                String.class
        );
        return extractCsrfToken(loginPage.getBody());
    }

    private String extractCsrfToken(String html) {
        return Jsoup.parse(html)
                    .selectFirst("input[name=_csrf]")
                    .val();
    }

    public ResponseEntity<Void> registerShippingAddress(RegisterShippingAddressRequest request) {
        return client.postForEntity("/my/shipping-addresses", request, Void.class);
    }

    public ResponseEntity<Void> updateShippingAddress(String shippingAddressId, UpdateShippingAddressRequest request) {
        return client.exchange(
                RequestEntity.put("/my/shipping-addresses/" + shippingAddressId)
                             .body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> removeShippingAddress(String shippingAddressId) {
        return client.exchange(
                RequestEntity.delete("/my/shipping-addresses/" + shippingAddressId).build(),
                Void.class
        );
    }

    public String registerShippingAddressThenGetId() {
        ResponseEntity<Void> response = registerShippingAddress(generateRegisterShippingAddressRequest());
        return response.getHeaders().getLocation().getPath().substring("/my/shipping-addresses/".length());
    }

    public TestRestTemplate unauthenticatedClient() {
        TestRestTemplate unauthenticated = new TestRestTemplate(new RestTemplateBuilder());
        unauthenticated.setUriTemplateHandler(client.getRestTemplate().getUriTemplateHandler());
        unauthenticated.getRestTemplate().setRequestFactory(noRedirectFactory());
        return unauthenticated;
    }

    private ClientHttpRequestFactory noRedirectFactory() {
        var httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    public ResponseEntity<Void> registerMember(MemberCreateRequest request) {
        return client.postForEntity("/members", request, Void.class);
    }
}
