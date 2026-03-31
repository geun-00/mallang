package io.mallang.member.domain.command;

import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;

public record AddShippingAddressCommand(
        Receiver receiver,
        Address address
) {
}
