package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.RemoveProductImageCommand;

public interface RemoveProductImageUseCase {

    void removeImage(RemoveProductImageCommand command);
}
