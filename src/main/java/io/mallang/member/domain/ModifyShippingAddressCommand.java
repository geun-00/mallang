package io.mallang.member.domain;

public record ModifyShippingAddressCommand(
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress
) {

}