package io.mallang.member.domain;

import io.mallang.domain.common.ClockHolder;
import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.command.AddShippingAddressCommand;
import io.mallang.member.domain.command.CreateMemberCommand;
import io.mallang.member.domain.command.RestoreMemberCommand;
import io.mallang.member.domain.command.ModifyShippingAddressCommand;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Member {

    private final MemberId id;

    private final Email email;

    private final Nickname nickname;

    private final Password password;

    private final LocalDateTime joinedAt;

    private final ShippingAddresses shippingAddresses;

    private MemberStatus status;

    private LocalDateTime withdrawnAt;

    private Member(MemberId id,
                   Email email,
                   Nickname nickname,
                   Password password,
                   LocalDateTime joinedAt) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.joinedAt = joinedAt;
        this.status = MemberStatus.ACTIVE;
        this.shippingAddresses = new ShippingAddresses();
    }

    public static Member restore(RestoreMemberCommand command) {
        Member member = new Member(command.id(), command.email(), command.nickname(), command.password(), command.joinedAt());
        member.status = command.status();
        member.withdrawnAt = command.withdrawnAt();
        member.shippingAddresses.restoreAll(command.shippingAddresses());
        return member;
    }

    public static Member create(CreateMemberCommand command, PasswordEncoder passwordEncoder, IdGenerator idGenerator, ClockHolder clockHolder) {
        return new Member(
                new MemberId(idGenerator.nextId()),
                command.email(),
                command.nickname(),
                Password.encode(command.password(), passwordEncoder),
                clockHolder.now()
        );
    }

    public boolean verifyPassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return password.verifyPassword(rawPassword, passwordEncoder);
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isOrderable() {
        return isActive();
    }

    public void withdraw(ClockHolder clockHolder) {
        if (!status.isActive())
            throw new IllegalStateException("ACTIVE 상태에서만 탈퇴할 수 있습니다.");

        this.status = MemberStatus.WITHDRAWN;
        this.withdrawnAt = clockHolder.now();
    }

    public ShippingAddress addShippingAddress(AddShippingAddressCommand command, IdGenerator idGenerator) {
        if (!status.isActive()) {
            throw new IllegalStateException("ACTIVE 상태에서만 배송지를 추가할 수 있습니다.");
        }

        return shippingAddresses.add(command, idGenerator);
    }

    public void setDefaultShippingAddress(ShippingAddressId shippingAddressId) {
        if (!status.isActive()) {
            throw new IllegalStateException("ACTIVE 상태에서만 기본 배송지를 변경할 수 있습니다.");
        }

        shippingAddresses.setDefault(shippingAddressId);
    }

    public ShippingAddress modifyShippingAddress(ShippingAddressId id, ModifyShippingAddressCommand command) {
        if (!status.isActive()) {
            throw new IllegalStateException("ACTIVE 상태에서만 배송지를 수정할 수 있습니다.");
        }

        return shippingAddresses.modify(id, command);
    }

    public void removeShippingAddress(ShippingAddressId id) {
        if (!status.isActive()) {
            throw new IllegalStateException("ACTIVE 상태에서만 배송지를 삭제할 수 있습니다.");
        }

        shippingAddresses.remove(id);
    }

    public List<ShippingAddress> getShippingAddresses() {
        return shippingAddresses.toList();
    }
}
