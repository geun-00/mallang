package io.mallang.member.domain.command;

import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;
import io.mallang.member.domain.ShippingAddressId;

public record RestoreShippingAddressCommand(
        ShippingAddressId id,
        Receiver receiver,
        Address address,
        boolean isDefault
) {
}
