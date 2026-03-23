package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.RegisterShippingAddressCommand;
import io.mallang.member.domain.ShippingAddressId;

public interface RegisterShippingAddressUseCase {

    ShippingAddressId register(RegisterShippingAddressCommand command);
}

