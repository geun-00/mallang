package io.mallang.product.adapter.persistence.jpa;

import io.mallang.common.adapter.persistence.jpa.BaseEntity;
import io.mallang.common.domain.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.*;
import io.mallang.product.domain.command.RestoreProductCommand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductJpaEntity extends BaseEntity {

    @Id
    @Column(name = "product_id")
    private String id;

    @Column(nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImageJpaEntity> images = new ArrayList<>();

    private ProductJpaEntity(
            String id,
            String sellerId,
            String name,
            String description,
            BigDecimal price,
            int stockQuantity,
            ProductStatus status,
            ProductCategory category
    ) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.category = category;
    }

    static ProductJpaEntity from(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity(
                product.getId().value(),
                product.getSellerId().value(),
                product.getName().value(),
                product.getDescription().value(),
                product.getPrice().value(),
                product.getStockQuantity().value(),
                product.getStatus(),
                product.getCategory()
        );

        ProductImage thumbnailImage = product.getThumbnailImage();
        if (thumbnailImage != null) {
            entity.images.add(ProductImageJpaEntity.fromThumbnail(thumbnailImage, entity));
        }

        product.getImages()
               .stream()
               .map(image -> ProductImageJpaEntity.fromImage(image, entity))
               .forEach(entity.images::add);

        return entity;
    }

    String getId() {
        return id;
    }

    Product toDomainWithImages() {
        Map<Boolean, List<ProductImageJpaEntity>> partitioned = images.stream()
                                                                      .collect(partitioningBy(ProductImageJpaEntity::isThumbnail));

        ProductImage thumbnailImage = partitioned.get(true).isEmpty()
                ? null
                : partitioned.get(true).getFirst().toDomain();

        List<ProductImage> otherImages = partitioned.get(false)
                                                    .stream()
                                                    .map(ProductImageJpaEntity::toDomain)
                                                    .toList();
        return Product.restore(
                new RestoreProductCommand(
                        new ProductId(id),
                        new MemberId(sellerId),
                        new ProductName(name),
                        new ProductDescription(description),
                        new Money(price),
                        new StockQuantity(stockQuantity),
                        status,
                        category,
                        thumbnailImage,
                        otherImages,
                        true
                )
        );
    }

    Product toDomain() {
        return Product.restore(
                new RestoreProductCommand(
                        new ProductId(id),
                        new MemberId(sellerId),
                        new ProductName(name),
                        new ProductDescription(description),
                        new Money(price),
                        new StockQuantity(stockQuantity),
                        status,
                        category,
                        null,
                        List.of(),
                        false
                )
        );
    }

    void updateFrom(Product product) {
        this.sellerId = product.getSellerId().value();
        this.name = product.getName().value();
        this.description = product.getDescription().value();
        this.price = product.getPrice().value();
        this.stockQuantity = product.getStockQuantity().value();
        this.status = product.getStatus();
        this.category = product.getCategory();

        if (product.isImagesLoaded()) {
            syncImages(product);
        }
    }

    private void syncImages(Product product) {
        Map<String, ProductImageJpaEntity> existingImagesById = indexExistingImagesById();
        Set<String> targetImageIds = collectTargetImageIds(product);

        removeImagesFrom(targetImageIds);
        upsertThumbnailImage(product.getThumbnailImage(), existingImagesById);
        upsertImages(product.getImages(), existingImagesById);
    }

    private Map<String, ProductImageJpaEntity> indexExistingImagesById() {
        return images.stream()
                     .collect(toMap(
                             ProductImageJpaEntity::getId,
                             identity())
                     );
    }

    private Set<String> collectTargetImageIds(Product product) {
        Set<String> targetImageIds = product.getImages()
                                            .stream()
                                            .map(image -> image.id().value())
                                            .collect(toSet());

        ProductImage thumbnailImage = product.getThumbnailImage();
        if (thumbnailImage != null) {
            targetImageIds.add(thumbnailImage.id().value());
        }

        return targetImageIds;
    }

    private void removeImagesFrom(Set<String> targetImageIds) {
        this.images.removeIf(image -> !targetImageIds.contains(image.getId()));
    }

    private void upsertThumbnailImage(
            ProductImage thumbnailImage,
            Map<String, ProductImageJpaEntity> existingImagesById
    ) {
        if (thumbnailImage == null) {
            return;
        }

        ProductImageJpaEntity existingImage = existingImagesById.get(thumbnailImage.id().value());

        if (existingImage == null) {
            this.images.add(ProductImageJpaEntity.fromThumbnail(thumbnailImage, this));
            return;
        }

        existingImage.updateFrom(thumbnailImage, true);
    }

    private void upsertImages(
            List<ProductImage> images,
            Map<String, ProductImageJpaEntity> existingImagesById
    ) {
        images.forEach(image -> {
            ProductImageJpaEntity existingImage = existingImagesById.get(image.id().value());

            if (existingImage == null) {
                this.images.add(ProductImageJpaEntity.fromImage(image, this));
                return;
            }

            existingImage.updateFrom(image, false);
        });
    }

}
