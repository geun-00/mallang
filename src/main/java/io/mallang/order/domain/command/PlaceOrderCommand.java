package io.mallang.order.domain.command;

import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;
import io.mallang.member.domain.MemberId;

import java.util.List;

public record PlaceOrderCommand(
        MemberId memberId,
        List<PlaceOrderItemCommand> items,
        Receiver receiver,
        Address address
) {
}
