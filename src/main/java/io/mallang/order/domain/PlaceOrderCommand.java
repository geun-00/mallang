package io.mallang.order.domain;

import java.util.List;

public record PlaceOrderCommand(
        String memberId,
        List<PlaceOrderItemCommand> items,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {
}
