package io.mallang.member.adapter.persistence.jpa;

import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.exception.MemberNotFoundException;
import io.mallang.member.domain.Nickname;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements SaveMemberPort, LoadMemberPort {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public void save(Member member) {
        MemberJpaEntity memberEntity = MemberJpaEntity.from(member);
        memberJpaRepository.save(memberEntity);
    }

    @Override
    public Member getById(MemberId memberId) {
        return memberJpaRepository.findById(memberId.value())
                                  .map(MemberJpaEntity::toDomain)
                                  .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    @Override
    public Member getByEmail(Email email) {
        return memberJpaRepository.findByEmail(email.address())
                                  .map(MemberJpaEntity::toDomain)
                                  .orElseThrow(() -> new MemberNotFoundException(email));
    }

    @Override
    public boolean existsByEmail(Email email) {
        return memberJpaRepository.existsByEmail(email.address());
    }

    @Override
    public boolean existsByNickname(Nickname nickname) {
        return memberJpaRepository.existsByNickname(nickname.value());
    }
}
