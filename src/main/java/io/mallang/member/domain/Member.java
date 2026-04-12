package io.mallang.member.domain;

import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.exception.AggregateNotLoadedException;
import io.mallang.member.domain.command.AddShippingAddressCommand;
import io.mallang.member.domain.command.CreateMemberCommand;
import io.mallang.member.domain.command.ModifyShippingAddressCommand;
import io.mallang.member.domain.command.RestoreMemberCommand;
import io.mallang.member.domain.exception.InvalidMemberStateException;
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

    private final boolean shippingAddressesLoaded;

    private MemberStatus status;

    private LocalDateTime withdrawnAt;

    private Member(
            MemberId id,
            Email email,
            Nickname nickname,
            Password password,
            LocalDateTime joinedAt
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.joinedAt = joinedAt;
        this.status = MemberStatus.ACTIVE;
        this.shippingAddresses = new ShippingAddresses();
        this.shippingAddressesLoaded = true;
    }

    private Member(
            MemberId id,
            Email email,
            Nickname nickname,
            Password password,
            LocalDateTime joinedAt,
            ShippingAddresses shippingAddresses,
            boolean shippingAddressesLoaded,
            MemberStatus status,
            LocalDateTime withdrawnAt
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.joinedAt = joinedAt;
        this.shippingAddresses = shippingAddresses;
        this.shippingAddressesLoaded = shippingAddressesLoaded;
        this.status = status;
        this.withdrawnAt = withdrawnAt;
    }

    public static Member create(
            CreateMemberCommand command,
            MemberPasswordEncoder passwordEncoder,
            IdGenerator idGenerator,
            ClockHolder clockHolder
    ) {
        return new Member(
                new MemberId(idGenerator.nextId()),
                command.email(),
                command.nickname(),
                Password.encode(command.password(), passwordEncoder),
                clockHolder.now()
        );
    }

    public static Member restore(RestoreMemberCommand command) {
        ShippingAddresses shippingAddresses = new ShippingAddresses();
        shippingAddresses.restoreAll(command.shippingAddresses());

        return new Member(
                command.id(),
                command.email(),
                command.nickname(),
                command.password(),
                command.joinedAt(),
                shippingAddresses,
                command.shippingAddressesLoaded(),
                command.status(),
                command.withdrawnAt()
        );
    }

    public boolean verifyPassword(String rawPassword, MemberPasswordEncoder passwordEncoder) {
        return password.verifyPassword(rawPassword, passwordEncoder);
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isOrderable() {
        return isActive();
    }

    public void withdraw(ClockHolder clockHolder) {
        validateActive("ACTIVE 상태에서만 탈퇴할 수 있습니다.");

        this.status = MemberStatus.WITHDRAWN;
        this.withdrawnAt = clockHolder.now();
    }

    public ShippingAddress addShippingAddress(AddShippingAddressCommand command, IdGenerator idGenerator) {
        validateActive("ACTIVE 상태에서만 배송지를 추가할 수 있습니다.");
        validateShippingAddressesLoaded();

        return shippingAddresses.add(command, idGenerator);
    }

    public void setDefaultShippingAddress(ShippingAddressId shippingAddressId) {
        validateActive("ACTIVE 상태에서만 기본 배송지를 변경할 수 있습니다.");
        validateShippingAddressesLoaded();

        shippingAddresses.setDefault(shippingAddressId);
    }

    public ShippingAddress modifyShippingAddress(ShippingAddressId id, ModifyShippingAddressCommand command) {
        validateActive("ACTIVE 상태에서만 배송지를 수정할 수 있습니다.");
        validateShippingAddressesLoaded();

        return shippingAddresses.modify(id, command);
    }

    public void removeShippingAddress(ShippingAddressId id) {
        validateActive("ACTIVE 상태에서만 배송지를 삭제할 수 있습니다.");
        validateShippingAddressesLoaded();

        shippingAddresses.remove(id);
    }

    public List<ShippingAddress> getShippingAddresses() {
        return shippingAddresses.toList();
    }

    private void validateActive(String message) {
        if (!status.isActive()) {
            throw new InvalidMemberStateException(message, id, status);
        }
    }

    private void validateShippingAddressesLoaded() {
        if (!shippingAddressesLoaded) {
            throw new AggregateNotLoadedException("배송지가 로딩되지 않은 회원입니다.");
        }
    }
}
