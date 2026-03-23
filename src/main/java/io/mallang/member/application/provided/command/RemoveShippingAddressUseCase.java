package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.RemoveShippingAddressCommand;

public interface RemoveShippingAddressUseCase {

    void remove(RemoveShippingAddressCommand command);
}
