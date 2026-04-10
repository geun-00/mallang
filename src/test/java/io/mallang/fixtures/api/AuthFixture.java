package io.mallang.fixtures.api;

import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.security.adapter.web.model.LoginRequest;
import io.mallang.security.adapter.web.model.LoginResponse;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

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
        authenticatedClient().getRestTemplate().getInterceptors().clear();

        ResponseEntity<LoginResponse> loginResponse = authenticatedClient().exchange(
                RequestEntity.post(LOGIN_API)
                        .body(new LoginRequest(email, password)),
                LoginResponse.class
        );

        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);
        String csrfToken = loginResponse.getBody().csrfToken();

        authenticatedClient().getRestTemplate().getInterceptors().clear();
        authenticatedClient().getRestTemplate().getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(COOKIE, sessionCookie);
            request.getHeaders().add("X-CSRF-TOKEN", csrfToken);
            return execution.execute(request, body);
        });
    }
}
