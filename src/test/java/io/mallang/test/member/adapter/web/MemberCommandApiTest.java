package io.mallang.test.member.adapter.web;

import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.MemberId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.MemberFixture.generateEmailValue;
import static io.mallang.fixtures.MemberFixture.generateNicknameValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberCommandApiTest {

    @Test
    void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        // given
        var request = generateCreateRequest();

        // when
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(@Autowired TestRestTemplate client) {
        // given
        var request = generateCreateRequest();

        // when
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request,
                Void.class
        );

        // then
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.getPath()).startsWith("/members");
        assertThat(location.getPath().replace("/members/", "")).isNotBlank();
    }

    @Test
    void 올바르게_요청하면_Location_헤더의_식별자로_회원을_조회할_수_있다(
            @Autowired TestRestTemplate client,
            @Autowired LoadMemberPort loadMemberPort
    ) {
        // given
        var request = generateCreateRequest();

        // when
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request,
                Void.class
        );

        // then
        String memberIdValue = response.getHeaders().getLocation().getPath().substring("/members/".length());

        assertThatCode(() -> loadMemberPort.getById(new MemberId(memberIdValue)))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "  ")
    void email_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidEmail,
            @Autowired TestRestTemplate client
    ) {
        // given
        var request = generateCreateRequest(invalidEmail);

        // when
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "  ")
    void password_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidPassword,
            @Autowired TestRestTemplate client
    ) {
        // given
        var request = new MemberCreateRequest(generateEmailValue(), invalidPassword, generateNicknameValue());

        // when
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "  ")
    void nickname_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidNickname,
            @Autowired TestRestTemplate client
    ) {
        // given
        var request = new MemberCreateRequest(generateEmailValue(), "password12@", invalidNickname);

        // when
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 이미_존재하는_이메일이면_409_Conflict_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        // given
        String email = generateEmailValue();
        var request1 = new MemberCreateRequest(email, "password12@", generateNicknameValue());

        client.postForEntity("/members", request1, Void.class);

        // when
        var request2 = new MemberCreateRequest(email, "password12@", generateNicknameValue());
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request2,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
    }

    @Test
    void 이미_존재하는_닉네임이면_409_Conflict_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        // given
        String nickname = generateNicknameValue();
        var request1 = new MemberCreateRequest(generateEmailValue(), "password12@", nickname);

        client.postForEntity("/members", request1, Void.class);

        // when
        var request2 = new MemberCreateRequest(generateEmailValue(), "password12@", nickname);
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request2,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestRestTemplate client) {
        // given
        var request = new MemberCreateRequest("invalid-email", "password12@", generateNicknameValue());

        // when
        ResponseEntity<Void> response = client.postForEntity(
                "/members",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}