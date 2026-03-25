package io.mallang.member.application.provided.command.model;

public record UpdateShippingAddressCommand(
        String memberIdValue,
        String shippingAddressIdValue,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {
}
