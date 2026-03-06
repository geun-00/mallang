package io.mallang.member.domain;

import io.mallang.domain.common.IdGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private List<ShippingAddress> shippingAddresses = new ArrayList<>();

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

    public ShippingAddress addShippingAddress(AddShippingAddressCommand addCommand, IdGenerator idGenerator) {
        state(this.status == MemberStatus.ACTIVE, "ACTIVE 상태에서만 배송지를 추가할 수 있습니다.");
        state(this.shippingAddresses.size() < 5, "배송지는 최대 5개까지 등록할 수 있습니다.");

        boolean isDefault = shippingAddresses.isEmpty();
        ShippingAddress shippingAddress = ShippingAddress.create(addCommand, isDefault, idGenerator);

        shippingAddresses.add(shippingAddress);

        return shippingAddress;
    }

    public void setDefaultShippingAddress(ShippingAddressId shippingAddressId) {
        state(this.status == MemberStatus.ACTIVE, "ACTIVE 상태에서만 기본 배송지를 변경할 수 있습니다.");

        ShippingAddress newDefaultAddress = getShippingAddress(shippingAddressId);

        shippingAddresses.forEach(ShippingAddress::unsetDefault);
        newDefaultAddress.setDefault();
    }

    public ShippingAddress modifyShippingAddress(ShippingAddressId originId, ModifyShippingAddressCommand modifyCommand) {
        state(this.status == MemberStatus.ACTIVE, "ACTIVE 상태에서만 배송지를 수정할 수 있습니다.");

        ShippingAddress originShippingAddress = getShippingAddress(originId);

        originShippingAddress.modify(modifyCommand);
        return originShippingAddress;
    }

    public void removeShippingAddress(ShippingAddressId removeId) {
        state(this.status == MemberStatus.ACTIVE, "ACTIVE 상태에서만 배송지를 삭제할 수 있습니다.");

        ShippingAddress removeShippingAddress = getShippingAddress(removeId);

        shippingAddresses.remove(removeShippingAddress);
    }

    private ShippingAddress getShippingAddress(ShippingAddressId originId) {
        return shippingAddresses.stream()
                                .filter(shippingAddress -> shippingAddress.getId().equals(originId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("해당 배송지 ID를 가진 배송지가 존재하지 않습니다."));
    }

    public List<ShippingAddress> getShippingAddresses() {
        return Collections.unmodifiableList(shippingAddresses);
    }
}
