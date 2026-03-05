package io.mallang.member.domain;

import io.mallang.domain.common.IdGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.util.Assert.state;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    private MemberId id;

    private Email email;

    private Password password;

    private Nickname nickname;

    private MemberStatus status;

    private LocalDateTime joinedAt;

    private LocalDateTime withdrawnAt;

    public static Member create(MemberCreateCommand createCommand, PasswordEncoder passwordEncoder, IdGenerator idGenerator) {
        Member member = new Member();

        member.id = new MemberId(idGenerator.nextId());
        member.email = new Email(createCommand.email());
        member.nickname = new Nickname(createCommand.nickname());
        member.password = Password.encode(createCommand.password(), passwordEncoder);

        member.status = MemberStatus.ACTIVE;
        member.joinedAt = LocalDateTime.now();

        return member;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isOrderable() {
        return isActive();
    }

    public void withdraw() {
        state(this.status == MemberStatus.ACTIVE, "ACTIVE 상태에서만 탈퇴할 수 있습니다.");

        this.status = MemberStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
    }
}
