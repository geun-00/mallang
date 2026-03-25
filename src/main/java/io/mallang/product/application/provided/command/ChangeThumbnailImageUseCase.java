package io.mallang.product.application.provided.command;

import io.mallang.product.application.provided.command.model.ChangeThumbnailImageCommand;

public interface ChangeThumbnailImageUseCase {

    void changeThumbnail(ChangeThumbnailImageCommand command);
}
