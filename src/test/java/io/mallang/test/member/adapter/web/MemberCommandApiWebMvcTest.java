package io.mallang.test.member.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.member.adapter.web.MemberCommandApi;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import io.mallang.test.support.web.WebMvcRequestTestSupport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static io.mallang.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcAdapterTest(MemberCommandApi.class)
public class MemberCommandApiWebMvcTest extends WebMvcRequestTestSupport {

    @MockitoBean
    RegisterMemberUseCase registerMemberUseCase;

    @Nested
    class 요청_검증 {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = "  ")
        void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String invalidEmail) throws JsonProcessingException {
            // given
            var request = generateCreateRequest(invalidEmail);

            // when
            MvcTestResult result = postJson("/api/v1/members", request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = "  ")
        void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String invalidPassword) throws JsonProcessingException {
            // given
            var request = new MemberCreateRequest(generateEmailValue(), invalidPassword, generateNicknameValue());

            // when
            MvcTestResult result = postJson("/api/v1/members", request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = "  ")
        void nickname_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String invalidNickname) throws JsonProcessingException {
            // given
            var request = new MemberCreateRequest(generateEmailValue(), DEFAULT_PASSWORD, invalidNickname);

            // when
            MvcTestResult result = postJson("/api/v1/members", request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }
    }
}
