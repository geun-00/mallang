package io.mallang.member.application.provided.command.model;

import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.ShippingAddressId;

public record UpdateShippingAddressCommand(
        MemberId memberId,
        ShippingAddressId shippingAddressId,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {
}
