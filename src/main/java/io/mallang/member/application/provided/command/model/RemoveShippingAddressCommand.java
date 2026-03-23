package io.mallang.member.application.provided.command.model;

import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.ShippingAddressId;

public record RemoveShippingAddressCommand(
        MemberId memberId,
        ShippingAddressId shippingAddressId
) {
}
