package io.mallang.test.member.application.service.command;

import io.mallang.common.application.event.EventPublisher;
import io.mallang.common.domain.exception.DuplicateException;
import io.mallang.fixtures.CommonFixture;
import io.mallang.member.application.event.MemberRegisteredEvent;
import io.mallang.member.application.provided.command.model.RegisterMemberCommand;
import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.application.service.command.MemberCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.mallang.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberCommandService Unit")
class MemberCommandServiceTest {

    @Test
    void 회원가입에_성공하면_MemberRegisteredEvent를_발행한다(
            @Mock SaveMemberPort saveMemberPort,
            @Mock LoadMemberPort loadMemberPort,
            @Mock EventPublisher eventPublisher
    ) {
        // given
        MemberCommandService memberCommandService = new MemberCommandService(
                CommonFixture.generateIdGenerator(),
                CommonFixture.generateClockHolder(),
                eventPublisher,
                saveMemberPort,
                loadMemberPort,
                generatePasswordEncoder()
        );
        RegisterMemberCommand command = generateRegisterCommand();
        given(loadMemberPort.existsByEmail(any())).willReturn(false);
        given(loadMemberPort.existsByNickname(any())).willReturn(false);

        // when
        RegisterMemberResult result = memberCommandService.register(command);

        // then
        ArgumentCaptor<MemberRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(MemberRegisteredEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().memberId().value()).isEqualTo(result.memberId());
    }

    @Test
    void 회원가입이_실패하면_MemberRegisteredEvent를_발행하지_않는다(
            @Mock SaveMemberPort saveMemberPort,
            @Mock LoadMemberPort loadMemberPort,
            @Mock EventPublisher eventPublisher
    ) {
        // given
        MemberCommandService memberCommandService = new MemberCommandService(
                CommonFixture.generateIdGenerator(),
                CommonFixture.generateClockHolder(),
                eventPublisher,
                saveMemberPort,
                loadMemberPort,
                generatePasswordEncoder()
        );
        RegisterMemberCommand command = new RegisterMemberCommand(
                generateEmailValue(),
                DEFAULT_PASSWORD,
                generateNicknameValue()
        );
        given(loadMemberPort.existsByEmail(any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberCommandService.register(command))
                .isInstanceOf(DuplicateException.class);
        verify(eventPublisher, never()).publish(any());
    }
}
