package io.mallang.member.application.service.command;

import io.mallang.domain.common.ClockHolder;
import io.mallang.domain.common.IdGenerator;
import io.mallang.member.application.provided.command.RegisterMemberUseCase;
import io.mallang.member.application.provided.command.model.RegisterMemberCommand;
import io.mallang.member.application.provided.command.model.RegisterMemberResult;
import io.mallang.member.domain.command.CreateMemberCommand;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.exception.DuplicateEmailException;
import io.mallang.member.domain.exception.DuplicateNicknameException;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.Nickname;
import io.mallang.member.domain.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberCommandService implements RegisterMemberUseCase {

    private final IdGenerator idGenerator;
    private final ClockHolder clockHolder;
    private final SaveMemberPort saveMemberPort;
    private final LoadMemberPort loadMemberPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterMemberResult register(RegisterMemberCommand command) {
        Email email = new Email(command.email());
        Nickname nickname = new Nickname(command.nickname());

        validateDuplicateEmail(email);
        validateDuplicateNickname(nickname);

        Member member = Member.create(new CreateMemberCommand(command.email(), command.password(), command.nickname()), passwordEncoder, idGenerator, clockHolder);
        saveMemberPort.save(member);

        return new RegisterMemberResult(member.getId().value());
    }

    private void validateDuplicateEmail(Email email) {
        if (loadMemberPort.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
    }

    private void validateDuplicateNickname(Nickname nickname) {
        if (loadMemberPort.existsByNickname(nickname)) {
            throw new DuplicateNicknameException(nickname);
        }
    }
}
