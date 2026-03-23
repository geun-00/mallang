package io.mallang.member.application.provided.command.model;

import io.mallang.member.domain.MemberId;

public record RegisterShippingAddressCommand(
        MemberId memberId,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {
}
