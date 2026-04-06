package io.mallang.fixtures.api;

import io.mallang.member.adapter.web.model.MemberCreateRequest;
import org.jsoup.Jsoup;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

public final class AuthFixture extends ApiFixture {

    private final MemberApiFixture memberFixture;

    public AuthFixture(FixtureContext context, MemberApiFixture memberFixture) {
        super(context);
        this.memberFixture = memberFixture;
    }

    public MemberCreateRequest createMemberThenLogin() {
        MemberCreateRequest request = generateCreateRequest();
        memberFixture.registerMember(request);
        login(request.email(), request.password());
        return request;
    }

    public void login(String email, String password) {
        String initialCookie = fetchInitialCookieWithCsrfToken();
        String sessionCookie = loginWithCsrf(email, password, initialCookie);
        String csrfToken = fetchCsrfTokenFromNewSession(sessionCookie);
        registerAuthInterceptor(sessionCookie, csrfToken);
    }

    private String fetchInitialCookieWithCsrfToken() {
        ResponseEntity<String> loginPage = authenticatedClient().getForEntity("/login", String.class);
        return loginPage.getHeaders().getFirst(SET_COOKIE);
    }

    private String loginWithCsrf(String email, String password, String initialCookie) {
        String csrfToken = fetchCsrfTokenFromCookie(initialCookie);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("email", email);
        form.add("password", password);
        form.add("_csrf", csrfToken);

        ResponseEntity<Void> loginResponse = unauthenticatedClient().exchange(
                RequestEntity.post("/login")
                             .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                             .header(COOKIE, initialCookie)
                             .body(form),
                Void.class
        );

        return loginResponse.getHeaders().getFirst(SET_COOKIE);
    }

    private String fetchCsrfTokenFromNewSession(String sessionCookie) {
        ResponseEntity<String> afterLoginPage = authenticatedClient().exchange(
                RequestEntity.get("/login")
                             .header(COOKIE, sessionCookie)
                             .build(),
                String.class
        );
        return extractCsrfToken(afterLoginPage.getBody());
    }

    private void registerAuthInterceptor(String sessionCookie, String csrfToken) {
        authenticatedClient().getRestTemplate().getInterceptors().clear();
        authenticatedClient().getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(COOKIE, sessionCookie);
            request.getHeaders().add("X-CSRF-TOKEN", csrfToken);
            return execution.execute(request, body);
        });
    }

    private String fetchCsrfTokenFromCookie(String initialCookie) {
        ResponseEntity<String> loginPage = authenticatedClient().exchange(
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
}
