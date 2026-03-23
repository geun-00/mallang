package io.mallang.member.application.provided.command;

import io.mallang.member.application.provided.command.model.UpdateDefaultShippingAddressCommand;

public interface UpdateDefaultShippingAddressUseCase {

    void update(UpdateDefaultShippingAddressCommand updateCommand);
}
