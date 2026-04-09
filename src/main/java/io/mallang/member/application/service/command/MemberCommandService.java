package io.mallang.member.application.service.command;

import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.exception.DuplicateException;
import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import io.mallang.member.application.provided.command.model.RegisterMemberCommand;
import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberPasswordEncoder;
import io.mallang.member.domain.Nickname;
import io.mallang.member.domain.command.CreateMemberCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements RegisterMemberUseCase {

    private final IdGenerator idGenerator;
    private final ClockHolder clockHolder;
    private final SaveCartPort saveCartPort;
    private final SaveMemberPort saveMemberPort;
    private final LoadMemberPort loadMemberPort;
    private final MemberPasswordEncoder passwordEncoder;

    @Override
    public RegisterMemberResult register(RegisterMemberCommand command) {
        Email email = new Email(command.email());
        Nickname nickname = new Nickname(command.nickname());

        validateDuplicateEmail(email);
        validateDuplicateNickname(nickname);

        CreateMemberCommand createCommand = new CreateMemberCommand(email, command.password(), nickname);
        Member member = Member.create(createCommand, passwordEncoder, idGenerator, clockHolder);
        saveMemberPort.save(member);
        saveCartPort.save(Cart.create(member.getId()));

        return new RegisterMemberResult(member.getId().value());
    }

    private void validateDuplicateEmail(Email email) {
        if (loadMemberPort.existsByEmail(email)) {
            throw new DuplicateException("이미 사용 중인 이메일입니다: " + email.address());
        }
    }

    private void validateDuplicateNickname(Nickname nickname) {
        if (loadMemberPort.existsByNickname(nickname)) {
            throw new DuplicateException("이미 사용 중인 닉네임입니다: " + nickname.value());
        }
    }
}
