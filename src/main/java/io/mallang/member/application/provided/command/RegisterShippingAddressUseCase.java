package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.RegisterShippingAddressCommand;
import io.mallang.member.application.provided.command.model.RegisterShippingAddressResult;

public interface RegisterShippingAddressUseCase {

    RegisterShippingAddressResult register(RegisterShippingAddressCommand command);
}

