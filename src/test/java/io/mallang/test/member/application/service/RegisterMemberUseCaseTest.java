package io.mallang.test.member.application.service;

import io.mallang.TestConfig;
import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.domain.exception.DuplicateEmailException;
import io.mallang.member.domain.exception.DuplicateNicknameException;
import io.mallang.member.domain.command.MemberCreateCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.mallang.fixtures.MemberFixture.generateCreateCommand;
import static io.mallang.fixtures.MemberFixture.generateEmailValue;
import static io.mallang.fixtures.MemberFixture.generateNicknameValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
class RegisterMemberUseCaseTest {

    @Autowired RegisterMemberUseCase registerMemberUseCase;

    @Test
    void 회원가입_성공시_MemberId를_반환한다() {
        // given
        MemberCreateCommand command = generateCreateCommand();

        // when
        RegisterMemberResult result = registerMemberUseCase.register(command);

        // then
        assertThat(result.memberId()).isNotNull();
    }

    @Test
    void 동일한_이메일로_중복_가입하면_DuplicateEmailException이_발생한다() {
        // given
        String email = generateEmailValue();
        registerMemberUseCase.register(new MemberCreateCommand(email, "password12@", generateNicknameValue()));

        // when & then
        assertThatThrownBy(() -> registerMemberUseCase.register(
                new MemberCreateCommand(email, "password12@", generateNicknameValue())))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void 동일한_닉네임으로_중복_가입하면_DuplicateNicknameException이_발생한다() {
        // given
        String nickname = generateNicknameValue();
        registerMemberUseCase.register(new MemberCreateCommand(generateEmailValue(), "password12@", nickname));

        // when & then
        assertThatThrownBy(() -> registerMemberUseCase.register(
                new MemberCreateCommand(generateEmailValue(), "password12@", nickname)))
                .isInstanceOf(DuplicateNicknameException.class);
    }
}
