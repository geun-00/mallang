package io.mallang.member.application.provided.command.model;

public record RemoveShippingAddressCommand(
        String memberIdValue,
        String shippingAddressIdValue
) {
}
