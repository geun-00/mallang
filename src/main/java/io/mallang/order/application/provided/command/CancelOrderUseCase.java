package io.mallang.order.application.provided.command;

import io.mallang.order.application.provided.command.model.CancelOrderCommand;

public interface CancelOrderUseCase {

    void cancel(CancelOrderCommand command);
}
