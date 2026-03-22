package io.mallang.member.domain.command;

public record AddShippingAddressCommand(
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {

}