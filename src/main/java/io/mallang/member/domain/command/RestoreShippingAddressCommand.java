package io.mallang.member.domain.command;

import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.member.domain.ShippingAddressId;

public record RestoreShippingAddressCommand(
        ShippingAddressId id,
        Receiver receiver,
        Address address,
        boolean isDefault
) {
}
