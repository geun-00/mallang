package io.mallang.order.application.provided.command;

import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderResult;

public interface CreateOrderUseCase {

    CreateOrderResult place(CreateOrderCommand command);
}
