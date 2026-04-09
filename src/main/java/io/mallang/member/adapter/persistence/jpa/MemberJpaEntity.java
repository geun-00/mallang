package io.mallang.member.adapter.persistence.jpa;

import io.mallang.common.adapter.persistence.jpa.BaseEntity;
import io.mallang.member.domain.*;
import io.mallang.member.domain.command.RestoreMemberCommand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseEntity {

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingAddressJpaEntity> shippingAddresses = new ArrayList<>();

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

    static MemberJpaEntity from(Member member) {
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

    Member toDomainWithShippingAddresses() {
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
                                 .toList(),
                true
        ));
    }

    Member toDomain() {
        return Member.restore(new RestoreMemberCommand(
                new MemberId(memberId),
                new Email(email),
                new Nickname(nickname),
                new Password(password),
                joinedAt,
                status,
                withdrawnAt,
                List.of(),
                false
        ));
    }

    void updateFrom(Member member) {
        this.email = member.getEmail().address();
        this.nickname = member.getNickname().value();
        this.password = member.getPassword().value();
        this.withdrawnAt = member.getWithdrawnAt();
        this.status = member.getStatus();

        if (member.isShippingAddressesLoaded()) {
            syncShippingAddresses(member.getShippingAddresses());
        }
    }

    private void syncShippingAddresses(List<ShippingAddress> addresses) {
        Map<String, ShippingAddressJpaEntity> existingById = indexExistingAddressesById();

        Set<String> targetIds = collectTargetAddressIds(addresses);
        removeAddressesFrom(targetIds);

        upsertAddresses(addresses, existingById);
    }

    private Map<String, ShippingAddressJpaEntity> indexExistingAddressesById() {
        return this.shippingAddresses.stream()
                                     .collect(toMap(
                                             ShippingAddressJpaEntity::getId,
                                             identity()
                                     ));
    }

    private Set<String> collectTargetAddressIds(List<ShippingAddress> shippingAddresses) {
        return shippingAddresses.stream()
                                .map(shippingAddress -> shippingAddress.getId().value())
                                .collect(toSet());
    }

    private void removeAddressesFrom(Set<String> targetIds) {
        this.shippingAddresses.removeIf(entity -> !targetIds.contains(entity.getId()));
    }

    private void upsertAddresses(
            List<ShippingAddress> shippingAddresses,
            Map<String, ShippingAddressJpaEntity> existingById
    ) {
        for (ShippingAddress shippingAddress : shippingAddresses) {
            ShippingAddressJpaEntity existing = existingById.get(shippingAddress.getId().value());

            if (existing == null) {
                addShippingAddress(shippingAddress);
                continue;
            }

            existing.updateFrom(shippingAddress);
        }
    }

    private void addShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddresses.add(ShippingAddressJpaEntity.from(shippingAddress, this));
    }
}
