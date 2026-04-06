package io.mallang.test.member.adapter.web;

import io.mallang.FixtureSession;
import io.mallang.annotations.WebAdapterTest;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static io.mallang.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.http.HttpStatus.*;

@WebAdapterTest
@DisplayName("MemberCommand API")
class MemberCommandApiTest {

    @Nested
    @DisplayName("POST /members")
    class 회원가입 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                var request = generateCreateRequest();

                // when
                ResponseEntity<Void> response = fixture.member().registerMember(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(@Autowired FixtureSession fixture) {
                // given
                var request = generateCreateRequest();

                // when
                ResponseEntity<Void> response = fixture.member().registerMember(request);

                // then
                URI location = response.getHeaders().getLocation();
                assertThat(location).isNotNull();
                assertThat(location.getPath()).startsWith("/members");
                assertThat(location.getPath().replace("/members/", "")).isNotBlank();
            }

            @Test
            void 올바르게_요청하면_Location_헤더의_식별자로_회원을_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadMemberPort loadMemberPort
            ) {
                // given
                var request = generateCreateRequest();

                // when
                String memberIdValue = fixture.member().registerMemberThenGetId(request);

                // then
                assertThatCode(() -> loadMemberPort.getById(new MemberId(memberIdValue)))
                        .doesNotThrowAnyException();
            }
        }

        @Nested
        class 요청_검증 {

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = "  ")
            void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidEmail,
                    @Autowired FixtureSession fixture
            ) {
                // given
                var request = generateCreateRequest(invalidEmail);

                // when
                ResponseEntity<Void> response = fixture.member().registerMember(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = "  ")
            void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidPassword,
                    @Autowired FixtureSession fixture
            ) {
                // given
                var request = new MemberCreateRequest(generateEmailValue(), invalidPassword, generateNicknameValue());

                // when
                ResponseEntity<Void> response = fixture.member().registerMember(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = "  ")
            void nickname_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidNickname,
                    @Autowired FixtureSession fixture
            ) {
                // given
                var request = new MemberCreateRequest(generateEmailValue(), DEFAULT_PASSWORD, invalidNickname);

                // when
                ResponseEntity<Void> response = fixture.member().registerMember(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 중복 {

            @Test
            void 이미_존재하는_이메일이면_409_Conflict_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                String email = generateEmailValue();
                var request1 = new MemberCreateRequest(email, DEFAULT_PASSWORD, generateNicknameValue());

                fixture.member().registerMember(request1);

                // when
                var request2 = new MemberCreateRequest(email, DEFAULT_PASSWORD, generateNicknameValue());
                ResponseEntity<Void> response = fixture.member().registerMember(request2);

                // then
                assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
            }

            @Test
            void 이미_존재하는_닉네임이면_409_Conflict_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                String nickname = generateNicknameValue();
                var request1 = new MemberCreateRequest(generateEmailValue(), DEFAULT_PASSWORD, nickname);

                fixture.member().registerMember(request1);

                // when
                var request2 = new MemberCreateRequest(generateEmailValue(), DEFAULT_PASSWORD, nickname);
                ResponseEntity<Void> response = fixture.member().registerMember(request2);

                // then
                assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                var request = new MemberCreateRequest("invalid-email", DEFAULT_PASSWORD, generateNicknameValue());

                // when
                ResponseEntity<Void> response = fixture.member().registerMember(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }
}
