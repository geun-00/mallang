package io.mallang.member.domain.command;

import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;

public record AddShippingAddressCommand(
        Receiver receiver,
        Address address
) {
}
