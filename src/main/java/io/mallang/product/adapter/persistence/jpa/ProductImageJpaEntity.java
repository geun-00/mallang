package io.mallang.product.adapter.persistence.jpa;

import io.mallang.product.domain.ImageUrl;
import io.mallang.product.domain.ProductImage;
import io.mallang.product.domain.ProductImageId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImageJpaEntity {

    @Id
    @Column(name = "product_image_id")
    private String id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isThumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductJpaEntity product;

    private ProductImageJpaEntity(String id, String imageUrl, boolean isThumbnail, ProductJpaEntity product) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
        this.product = product;
    }

    public static ProductImageJpaEntity fromThumbnail(ProductImage image, ProductJpaEntity product) {
        return new ProductImageJpaEntity(
                image.id().value(),
                image.imageUrl().value(),
                true,
                product
        );
    }

    public static ProductImageJpaEntity fromImage(ProductImage image, ProductJpaEntity product) {
        return new ProductImageJpaEntity(
                image.id().value(),
                image.imageUrl().value(),
                false,
                product
        );
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public ProductImage toDomain() {
        return new ProductImage(
                new ProductImageId(id),
                new ImageUrl(imageUrl)
        );
    }
}
