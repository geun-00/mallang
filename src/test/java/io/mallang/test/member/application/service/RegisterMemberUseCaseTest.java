package io.mallang.test.member.application.service;

import io.mallang.annotations.UseCaseTest;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.common.domain.exception.DuplicateException;
import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import io.mallang.member.application.provided.command.model.RegisterMemberCommand;
import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.domain.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;

@UseCaseTest
@DisplayName("RegisterMember UseCase")
class RegisterMemberUseCaseTest {

    @Test
    void 회원가입_성공시_MemberId를_반환한다(
            @Autowired RegisterMemberUseCase registerMemberUseCase
    ) {
        // given
        RegisterMemberCommand command = generateRegisterCommand();

        // when
        RegisterMemberResult result = registerMemberUseCase.register(command);

        // then
        assertThat(result.memberId()).isNotNull();
    }

    @Test
    void 회원가입_성공시_장바구니도_함께_생성된다(
            @Autowired RegisterMemberUseCase registerMemberUseCase,
            @Autowired LoadCartPort loadCartPort
    ) {
        // given
        RegisterMemberCommand command = generateRegisterCommand();

        // when
        RegisterMemberResult result = registerMemberUseCase.register(command);

        // then
        assertThatCode(() -> loadCartPort.getByMemberId(new MemberId(result.memberId())))
                .doesNotThrowAnyException();
    }

    @Test
    void 동일한_이메일로_중복_가입하면_DuplicateException이_발생한다(
            @Autowired RegisterMemberUseCase registerMemberUseCase
    ) {
        // given
        String email = generateEmailValue();
        registerMemberUseCase.register(new RegisterMemberCommand(email, DEFAULT_PASSWORD, generateNicknameValue()));

        // when & then
        assertThatThrownBy(() -> registerMemberUseCase.register(
                new RegisterMemberCommand(email, DEFAULT_PASSWORD, generateNicknameValue())))
                .isInstanceOf(DuplicateException.class);
    }

    @Test
    void 동일한_닉네임으로_중복_가입하면_DuplicateException이_발생한다(
            @Autowired RegisterMemberUseCase registerMemberUseCase
    ) {
        // given
        String nickname = generateNicknameValue();
        registerMemberUseCase.register(new RegisterMemberCommand(generateEmailValue(), DEFAULT_PASSWORD, nickname));

        // when & then
        assertThatThrownBy(() -> registerMemberUseCase.register(
                new RegisterMemberCommand(generateEmailValue(), DEFAULT_PASSWORD, nickname)))
                .isInstanceOf(DuplicateException.class);
    }
}
