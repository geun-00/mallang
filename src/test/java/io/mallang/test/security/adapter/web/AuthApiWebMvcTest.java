package io.mallang.test.security.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.security.adapter.web.AuthApi;
import io.mallang.security.adapter.web.model.LoginRequest;
import io.mallang.test.support.web.WebMvcRequestTestSupport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static io.mallang.fixtures.MemberFixture.DEFAULT_PASSWORD;
import static io.mallang.fixtures.MemberFixture.generateEmailValue;
import static io.mallang.fixtures.api.ApiFixture.LOGIN_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@WebMvcAdapterTest(AuthApi.class)
class AuthApiWebMvcTest extends WebMvcRequestTestSupport {

    @MockitoBean
    AuthenticationManager authenticationManager;

    @Nested
    class 로그인_요청_검증 {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " "})
        void email_속성이_비어있으면_400_Bad_Request_상태코드를_반환한다(String invalidEmail) throws JsonProcessingException {
            // given
            var request = new LoginRequest(invalidEmail, DEFAULT_PASSWORD);

            // when
            MvcTestResult result = postJson(LOGIN_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " "})
        void password_속성이_비어있으면_400_Bad_Request_상태코드를_반환한다(String invalidPassword) throws JsonProcessingException {
            // given
            var request = new LoginRequest(generateEmailValue(), invalidPassword);

            // when
            MvcTestResult result = postJson(LOGIN_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
