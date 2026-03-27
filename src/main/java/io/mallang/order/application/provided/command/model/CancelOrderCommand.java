package io.mallang.order.application.provided.command.model;

public record CancelOrderCommand(
        String orderIdValue,
        String memberIdValue
) {
}
