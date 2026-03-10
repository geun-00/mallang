package io.mallang.product.domain;

import io.mallang.domain.common.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class ProductImages {

    private static final int MAX_OTHER_IMAGES = 10;

    private ProductImage thumbnailImage;

    private List<ProductImage> images;

    private ProductImages(ProductImage thumbnailImage, List<ProductImage> images) {
        this.thumbnailImage = thumbnailImage;
        this.images = images;
    }

    static ProductImages from(ProductCreateCommand command, IdGenerator idGenerator) {
        if (command.images().isEmpty()) {
            return new ProductImages(null, new ArrayList<>());
        }

        long thumbnailCount = command.images().stream().filter(ProductImageCommand::isThumbnail).count();
        if (thumbnailCount != 1)
            throw new IllegalArgumentException("이미지가 있는 경우 대표 이미지는 반드시 하나여야 합니다.");

        long nonThumbnailCount = command.images().size() - 1;
        if (nonThumbnailCount > MAX_OTHER_IMAGES)
            throw new IllegalArgumentException("대표 이미지 외 이미지는 최대 10개까지 등록할 수 있습니다.");

        ProductImage thumbnailImage = command.images().stream()
                                             .filter(ProductImageCommand::isThumbnail)
                                             .findFirst()
                                             .map(imageCommand -> new ProductImage(
                                                     new ProductImageId(idGenerator.nextId()),
                                                     new ImageUrl(imageCommand.imageUrl())
                                             ))
                                             .orElse(null);

        List<ProductImage> otherImages = command.images().stream()
                                                .filter(imageCommand -> !imageCommand.isThumbnail())
                                                .map(imageCommand -> new ProductImage(
                                                        new ProductImageId(idGenerator.nextId()),
                                                        new ImageUrl(imageCommand.imageUrl())
                                                ))
                                                .collect(Collectors.toCollection(ArrayList::new));

        return new ProductImages(thumbnailImage, otherImages);
    }

    ProductImage getThumbnailImage() {
        return thumbnailImage;
    }

    List<ProductImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    void add(List<AddProductImageCommand> addCommands, IdGenerator idGenerator) {
        List<ProductImage> addedImages = addCommands.stream()
                                                  .map(command -> new ProductImage(
                                                          new ProductImageId(idGenerator.nextId()),
                                                          new ImageUrl(command.imageUrl())
                                                  ))
                                                  .toList();

        if (thumbnailImage == null) {
            this.thumbnailImage = addedImages.getFirst();
            this.images.addAll(addedImages.subList(1, addedImages.size()));
            return;
        }

        this.images.addAll(addedImages);
    }
}
