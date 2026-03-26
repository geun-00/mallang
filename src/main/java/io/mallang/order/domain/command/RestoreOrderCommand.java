package io.mallang.order.domain.command;

import io.mallang.member.domain.MemberId;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.OrderItem;
import io.mallang.order.domain.OrderStatus;
import io.mallang.order.domain.ShippingInfo;

import java.time.LocalDateTime;
import java.util.List;

public record RestoreOrderCommand(
        OrderId id,
        MemberId memberId,
        List<OrderItem> items,
        ShippingInfo shippingInfo,
        OrderStatus status,
        LocalDateTime orderedAt
) {
}
