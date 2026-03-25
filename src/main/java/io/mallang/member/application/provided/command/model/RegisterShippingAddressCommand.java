package io.mallang.member.application.provided.command.model;

public record RegisterShippingAddressCommand(
        String memberIdValue,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {
}
