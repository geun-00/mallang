package io.mallang.member.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.member.domain.command.AddShippingAddressCommand;
import io.mallang.member.domain.command.ModifyShippingAddressCommand;
import io.mallang.member.domain.command.RestoreShippingAddressCommand;
import lombok.Getter;

@Getter
public class ShippingAddress {

    private final ShippingAddressId id;

    private Receiver receiver;

    private Address address;

    private boolean isDefault;

    private ShippingAddress(ShippingAddressId id, Receiver receiver, Address address, boolean isDefault) {
        this.id = id;
        this.receiver = receiver;
        this.address = address;
        this.isDefault = isDefault;
    }

    public static ShippingAddress restore(RestoreShippingAddressCommand command) {
        return new ShippingAddress(command.id(), command.receiver(), command.address(), command.isDefault());
    }

    static ShippingAddress create(AddShippingAddressCommand command, boolean isDefault, IdGenerator idGenerator) {
        return new ShippingAddress(
                new ShippingAddressId(idGenerator.nextId()),
                new Receiver(command.receiverName(), command.receiverPhoneNumber()),
                new Address(command.zipCode(), command.mainAddress(), command.detailAddress()),
                isDefault
        );
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
