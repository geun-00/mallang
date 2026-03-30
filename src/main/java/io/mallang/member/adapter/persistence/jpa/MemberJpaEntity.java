package io.mallang.member.adapter.persistence.jpa;

import io.mallang.member.domain.*;
import io.mallang.member.domain.command.RestoreMemberCommand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity {

    @Id
    @Column(name = "member_id")
    private String memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    private LocalDateTime withdrawnAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private final List<ShippingAddressJpaEntity> shippingAddresses = new ArrayList<>();

    private MemberJpaEntity(
            String memberId,
            String email,
            String nickname,
            String password,
            LocalDateTime joinedAt,
            LocalDateTime withdrawnAt,
            MemberStatus status
    ) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.joinedAt = joinedAt;
        this.withdrawnAt = withdrawnAt;
        this.status = status;
    }

    public static MemberJpaEntity from(Member member) {
        MemberJpaEntity entity = new MemberJpaEntity(
                member.getId().value(),
                member.getEmail().address(),
                member.getNickname().value(),
                member.getPassword().value(),
                member.getJoinedAt(),
                member.getWithdrawnAt(),
                member.getStatus()
        );

        member.getShippingAddresses()
              .stream()
              .map(shippingAddress -> ShippingAddressJpaEntity.from(shippingAddress, entity))
              .forEach(entity.shippingAddresses::add);

        return entity;
    }

    public Member toDomain() {
        return Member.restore(new RestoreMemberCommand(
                new MemberId(memberId),
                new Email(email),
                new Nickname(nickname),
                new Password(password),
                joinedAt,
                status,
                withdrawnAt,
                shippingAddresses.stream()
                                 .map(ShippingAddressJpaEntity::toDomain)
                                 .toList()
        ));
    }
}
