package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.AddProductImagesCommand;

public interface AddProductImagesUseCase {

    void addImages(AddProductImagesCommand command);
}
