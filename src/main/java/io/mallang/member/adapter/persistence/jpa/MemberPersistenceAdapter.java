package io.mallang.member.adapter.persistence.jpa;

import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.MemberNotFoundException;
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
}
