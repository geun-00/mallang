package io.mallang.test.security.adapter.web;

import io.mallang.annotations.WebAdapterTest;
import io.mallang.fixtures.api.FixtureSession;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.security.adapter.web.model.LoginRequest;
import io.mallang.security.adapter.web.model.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.MemberFixture.DEFAULT_PASSWORD;
import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.ProductFixture.generateCreateProductRequest;
import static io.mallang.fixtures.api.ApiFixture.LOGIN_API;
import static io.mallang.fixtures.api.ApiFixture.LOGOUT_API;
import static io.mallang.fixtures.api.ApiFixture.PRODUCTS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@WebAdapterTest
@DisplayName("Auth API")
class AuthApiTest {

    @Nested
    @DisplayName("POST /login")
    class 로그인 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_200_Ok_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                MemberCreateRequest request = generateCreateRequest();
                fixture.member().registerMember(request);

                // when
                ResponseEntity<LoginResponse> response = fixture.auth().unauthenticatedClient().exchange(
                        RequestEntity.post(LOGIN_API).body(new LoginRequest(request.email(), request.password())),
                        LoginResponse.class
                );

                // then
                assertThat(response.getStatusCode()).isEqualTo(OK);
            }

            @Test
            void 올바르게_요청하면_세션_쿠키와_CSRF_토큰을_반환한다(@Autowired FixtureSession fixture) {
                // given
                MemberCreateRequest request = generateCreateRequest();
                fixture.member().registerMember(request);

                // when
                ResponseEntity<LoginResponse> response = fixture.auth().unauthenticatedClient().exchange(
                        RequestEntity.post(LOGIN_API).body(new LoginRequest(request.email(), request.password())),
                        LoginResponse.class
                );

                // then
                assertThat(response.getHeaders().getFirst(SET_COOKIE)).contains("JSESSIONID=");
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().csrfToken()).isNotBlank();
            }

            @Test
            void 올바르게_로그인하면_반환된_세션_쿠키와_CSRF_토큰으로_보호된_API에_접근할_수_있다(@Autowired FixtureSession fixture) {
                // given
                MemberCreateRequest memberRequest = generateCreateRequest();
                fixture.member().registerMember(memberRequest);

                ResponseEntity<LoginResponse> loginResponse = fixture.auth().unauthenticatedClient().exchange(
                        RequestEntity.post(LOGIN_API)
                                     .body(new LoginRequest(memberRequest.email(), memberRequest.password())),
                        LoginResponse.class
                );

                String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);
                String csrfToken = loginResponse.getBody().csrfToken();

                HttpHeaders headers = new HttpHeaders();
                headers.add(COOKIE, sessionCookie);
                headers.add("X-CSRF-TOKEN", csrfToken);

                CreateProductRequest request = generateCreateProductRequest();

                // when
                ResponseEntity<Void> response = fixture.product()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.post(PRODUCTS_API)
                                                                            .headers(headers)
                                                                            .body(request),
                                                               Void.class
                                                       );

                // then
                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }
        }

        @Nested
        class 실패 {

            @Test
            void 비밀번호가_올바르지_않으면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                MemberCreateRequest request = generateCreateRequest();
                fixture.member().registerMember(request);

                // when
                ResponseEntity<Void> response = fixture.auth().unauthenticatedClient().exchange(
                        RequestEntity.post(LOGIN_API)
                                     .body(new LoginRequest(request.email(), DEFAULT_PASSWORD + "wrong")),
                        Void.class
                );

                // then
                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }
    }

    @Nested
    @DisplayName("POST /logout")
    class 로그아웃 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();

                // when
                ResponseEntity<Void> response = fixture.auth()
                                                       .client()
                                                       .exchange(RequestEntity.post(LOGOUT_API).build(), Void.class);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }

            @Test
            void 로그아웃_후에는_같은_세션_쿠키와_CSRF_토큰으로_보호된_API에_접근할_수_없다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                fixture.auth().client().exchange(RequestEntity.post(LOGOUT_API).build(), Void.class);

                // when
                ResponseEntity<Void> response = fixture.product().registerProduct(generateCreateProductRequest());

                // then
                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }
    }
}
