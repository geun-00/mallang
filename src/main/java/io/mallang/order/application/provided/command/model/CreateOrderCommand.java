package io.mallang.order.application.provided.command.model;

import java.util.List;

public record CreateOrderCommand(
        String memberIdValue,
        List<CreateOrderItemCommand> items,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {
}
