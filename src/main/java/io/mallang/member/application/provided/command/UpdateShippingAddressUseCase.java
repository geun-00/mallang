package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.UpdateShippingAddressCommand;

public interface UpdateShippingAddressUseCase {

    void update(UpdateShippingAddressCommand command);
}
