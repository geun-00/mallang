package io.mallang.product.application.provided.command.model;

public record RegisterProductImageCommand(String imageUrl, boolean isThumbnail) {
}
