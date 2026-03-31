package io.mallang.member.adapter.persistence.jpa;

import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.Nickname;
import io.mallang.member.domain.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements SaveMemberPort, LoadMemberPort {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    @Transactional
    public void save(Member member) {
        memberJpaRepository.findById(member.getId().value())
                           .ifPresentOrElse(
                                   entity -> entity.updateFrom(member),
                                   () -> memberJpaRepository.save(MemberJpaEntity.from(member))
                           );
    }

    @Override
    public Member getById(MemberId memberId) {
        return memberJpaRepository.findById(memberId.value())
                                  .map(MemberJpaEntity::toDomain)
                                  .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    @Override
    public Member getByIdWithAddresses(MemberId memberId) {
        return memberJpaRepository.findWithShippingAddressesByMemberId(memberId.value())
                                  .map(MemberJpaEntity::toDomainWithShippingAddresses)
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
