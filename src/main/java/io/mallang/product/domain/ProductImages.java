package io.mallang.product.domain;

import io.mallang.domain.common.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ProductImages {

    private static final int MAX_OTHER_IMAGES = 10;

    private final List<ProductImage> images;

    private ProductImage thumbnailImage;

    private ProductImages(ProductImage thumbnailImage, List<ProductImage> images) {
        this.thumbnailImage = thumbnailImage;
        this.images = images;
    }

    static ProductImages restore(ProductImage thumbnailImage, List<ProductImage> images) {
        return new ProductImages(thumbnailImage, new ArrayList<>(images));
    }

    static ProductImages from(List<ProductImageCommand> images, IdGenerator idGenerator) {
        if (images.isEmpty()) {
            return new ProductImages(null, new ArrayList<>());
        }

        Map<Boolean, List<ProductImageCommand>> partitioned = images.stream()
                                                                    .collect(Collectors.partitioningBy(ProductImageCommand::isThumbnail));

        List<ProductImageCommand> thumbnail = partitioned.get(true);
        List<ProductImageCommand> others = partitioned.get(false);

        if (thumbnail.size() != 1)
            throw new IllegalArgumentException("이미지가 있는 경우 대표 이미지는 반드시 하나여야 합니다.");

        if (others.size() > MAX_OTHER_IMAGES)
            throw new IllegalArgumentException("대표 이미지 외 이미지는 최대 10개까지 등록할 수 있습니다.");

        ProductImage thumbnailImage = new ProductImage(
                new ProductImageId(idGenerator.nextId()),
                new ImageUrl(thumbnail.getFirst().imageUrl())
        );

        List<ProductImage> otherImages = others.stream()
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
        if (addCommands.isEmpty()) {
            return;
        }

        validateSize(addCommands);

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

    private void validateSize(List<AddProductImageCommand> addCommands) {
        int addedImagesCount = (thumbnailImage == null)
                ? addCommands.size() - 1
                : addCommands.size();
        if (this.images.size() + addedImagesCount > MAX_OTHER_IMAGES)
            throw new IllegalArgumentException("대표 이미지 외 이미지는 최대 10개까지 등록할 수 있습니다.");
    }

    void removeImage(ProductImageId imageId) {
        if (thumbnailImage != null && thumbnailImage.id().equals(imageId)) {
            this.thumbnailImage = (this.images.isEmpty()) ? null : this.images.removeFirst();
            return;
        }

        ProductImage removedProductImage = getProductImage(imageId);
        this.images.remove(removedProductImage);
    }

    public void changeThumbnailImage(ProductImageId imageId) {
        if (thumbnailImage != null && thumbnailImage.id().equals(imageId)) {
            return;
        }

        ProductImage newThumbnail = getProductImage(imageId);

        if (thumbnailImage != null) {
            this.images.add(thumbnailImage);
        }

        this.images.remove(newThumbnail);
        this.thumbnailImage = newThumbnail;
    }

    private ProductImage getProductImage(ProductImageId imageId) {
        return this.images.stream()
                          .filter(image -> image.id().equals(imageId))
                          .findFirst()
                          .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 존재하지 않습니다."));
    }
}
