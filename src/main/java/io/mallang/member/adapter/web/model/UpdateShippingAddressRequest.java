package io.mallang.member.adapter.web.model;

import jakarta.validation.constraints.NotBlank;

public record UpdateShippingAddressRequest(
        @NotBlank String receiverName,
        @NotBlank String receiverPhoneNumber,
        @NotBlank String zipCode,
        @NotBlank String mainAddress,
        String detailAddress
) {
}
