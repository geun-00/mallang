package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.DiscontinueProductCommand;

public interface DiscontinueProductUseCase {

    void discontinue(DiscontinueProductCommand command);
}
