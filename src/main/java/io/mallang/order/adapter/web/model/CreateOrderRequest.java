package io.mallang.order.adapter.web.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequest(
        @NotNull @Valid List<@NotNull @Valid CreateOrderItemRequest> items,
        @NotBlank String receiverName,
        @NotBlank String receiverPhoneNumber,
        @NotBlank String zipCode,
        @NotBlank String mainAddress,
        String detailAddress
) {
    public record CreateOrderItemRequest(
            @NotBlank String productId,
            @NotNull @Positive Integer quantity
    ) {
    }
}
