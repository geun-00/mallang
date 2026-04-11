package io.mallang.test.member.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mallang.member.adapter.web.MemberCommandApi;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static io.mallang.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MemberCommandApi.class)
public class MemberCommandApiWebMvcTest {

    @MockitoBean
    RegisterMemberUseCase registerMemberUseCase;

    @Nested
    class 요청_검증 {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = "  ")
        void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidEmail,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = generateCreateRequest(invalidEmail);

            // when
            MvcTestResult result = client.post()
                                         .uri("/api/v1/members")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = "  ")
        void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidPassword,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new MemberCreateRequest(generateEmailValue(), invalidPassword, generateNicknameValue());

            // when & then
            assertThat(client.post()
                             .uri("/api/v1/members")
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(objectMapper.writeValueAsString(request))
            ).apply(print())
             .hasStatus(BAD_REQUEST);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = "  ")
        void nickname_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidNickname,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new MemberCreateRequest(generateEmailValue(), DEFAULT_PASSWORD, invalidNickname);

            // when & then
            assertThat(client.post()
                             .uri("/api/v1/members")
                             .contentType(MediaType.APPLICATION_JSON)
                             .content(objectMapper.writeValueAsString(request))
            ).apply(print())
             .hasStatus(BAD_REQUEST);
        }
    }
}
