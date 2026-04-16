package io.mallang.product.adapter.persistence.jpa;

import io.mallang.common.adapter.persistence.jpa.BaseEntity;
import io.mallang.product.domain.ImageUrl;
import io.mallang.product.domain.ProductImage;
import io.mallang.product.domain.ProductImageId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_images", indexes = {@Index(name = "ux_product_images_thumbnail", columnList = "product_id, is_thumbnail")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ProductImageJpaEntity extends BaseEntity {

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

    static ProductImageJpaEntity fromThumbnail(ProductImage image, ProductJpaEntity product) {
        return new ProductImageJpaEntity(
                image.id()
                     .value(), image.imageUrl()
                                    .value(), true, product
        );
    }

    static ProductImageJpaEntity fromImage(ProductImage image, ProductJpaEntity product) {
        return new ProductImageJpaEntity(
                image.id()
                     .value(), image.imageUrl()
                                    .value(), false, product
        );
    }

    boolean isThumbnail() {
        return isThumbnail;
    }

    String getId() {
        return id;
    }

    ProductImage toDomain() {
        return new ProductImage(new ProductImageId(id), new ImageUrl(imageUrl));
    }

    void updateFrom(ProductImage image, boolean thumbnail) {
        this.imageUrl = image.imageUrl()
                             .value();
        this.isThumbnail = thumbnail;
    }
}
