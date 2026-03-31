package io.mallang.product.domain.command;

import io.mallang.product.domain.ImageUrl;

public record CreateProductImageCommand(ImageUrl imageUrl, boolean isThumbnail) {
}
