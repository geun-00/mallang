package io.mallang.product.application.provided.command.model;

public record ChangeThumbnailImageCommand(
        String memberIdValue,
        String productIdValue,
        String productImageIdValue
) {
}
