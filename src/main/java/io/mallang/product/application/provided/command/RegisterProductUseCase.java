package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductResult;

public interface RegisterProductUseCase {

    RegisterProductResult register(RegisterProductCommand command);
}
