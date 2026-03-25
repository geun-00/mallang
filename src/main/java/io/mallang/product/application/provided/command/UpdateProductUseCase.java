package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.UpdateProductCommand;

public interface UpdateProductUseCase {

    void update(UpdateProductCommand command);
}
