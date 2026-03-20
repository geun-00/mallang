package io.mallang.member.domain;

import io.mallang.domain.common.Address;
import io.mallang.domain.common.Receiver;

public record ShippingAddressRestoreCommand(
        ShippingAddressId id,
        Receiver receiver,
        Address address,
        boolean isDefault
) {
}
