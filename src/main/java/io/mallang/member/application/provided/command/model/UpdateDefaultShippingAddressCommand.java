package io.mallang.member.application.provided.command.model;

public record UpdateDefaultShippingAddressCommand(
        String memberIdValue,
        String shippingAddressIdValue
) {
}
