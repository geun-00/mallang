package io.mallang.member.domain.command;

import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;

public record ModifyShippingAddressCommand(
        Receiver receiver,
        Address address
) {
}
