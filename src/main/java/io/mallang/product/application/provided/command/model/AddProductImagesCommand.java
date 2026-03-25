package io.mallang.product.application.provided.command.model;

import java.util.List;

public record AddProductImagesCommand(
        String memberIdValue,
        String productIdValue,
        List<String> imageUrls
) {
}
