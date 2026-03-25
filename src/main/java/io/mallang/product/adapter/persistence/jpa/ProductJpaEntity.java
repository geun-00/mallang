package io.mallang.product.adapter.persistence.jpa;

import io.mallang.domain.common.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.partitioningBy;

@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductJpaEntity {

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImageJpaEntity> images = new ArrayList<>();

    private ProductJpaEntity(String id,
                             String sellerId,
                             String name,
                             String description,
                             BigDecimal price,
                             int stockQuantity,
                             ProductStatus status,
                             ProductCategory category) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.category = category;
    }

    public static ProductJpaEntity from(Product product) {
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

        product.getImages().stream()
               .map(image -> ProductImageJpaEntity.fromImage(image, entity))
               .forEach(entity.images::add);

        return entity;
    }

    public Product toDomainWithImages() {
        Map<Boolean, List<ProductImageJpaEntity>> partitioned =
                images.stream().collect(partitioningBy(ProductImageJpaEntity::isThumbnail));

        ProductImage thumbnailImage = partitioned.get(true).isEmpty()
                ? null
                : partitioned.get(true).getFirst().toDomain();

        List<ProductImage> otherImages = partitioned.get(false).stream()
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

    public Product toDomainWithoutImages() {
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
}
