package io.mallang.product.domain.command;

public record CreateProductImageCommand(String imageUrl, boolean isThumbnail) {
}
