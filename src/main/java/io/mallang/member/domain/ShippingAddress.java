package io.mallang.member.domain;

import io.mallang.domain.common.IdGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShippingAddress {

    private ShippingAddressId id;

    private Receiver receiver;

    private Address address;

    private boolean isDefault;

    static ShippingAddress create(AddShippingAddressCommand command, boolean isDefault, IdGenerator idGenerator) {
        ShippingAddress shippingAddress = new ShippingAddress();

        shippingAddress.id = new ShippingAddressId(idGenerator.nextId());
        shippingAddress.receiver = new Receiver(command.receiverName(), command.receiverPhoneNumber());
        shippingAddress.address = new Address(command.zipCode(), command.mainAddress(), command.detailAddress());
        shippingAddress.isDefault = isDefault;

        return shippingAddress;
    }

    void modify(ModifyShippingAddressCommand command) {
        this.receiver = new Receiver(command.receiverName(), command.receiverPhoneNumber());
        this.address = new Address(command.zipCode(), command.mainAddress(), command.detailAddress());
    }

    void unsetDefault() {
        this.isDefault = false;
    }

    void setDefault() {
        this.isDefault = true;
    }
}
