package io.mallang.product.domain;

import io.mallang.common.domain.exception.AggregateNotLoadedException;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.command.AddProductImageCommand;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.ModifyProductCommand;
import io.mallang.product.domain.command.RestoreProductCommand;
import io.mallang.product.domain.exception.NotProductSellerException;
import lombok.Getter;

import java.util.List;

@Getter
public class Product {

    private final ProductId id;

    private final MemberId sellerId;

    private final ProductImages productImages;

    private ProductName name;

    private ProductDescription description;

    private Money price;

    private ProductStatus status;

    private ProductCategory category;

    private final boolean imagesLoaded;

    private Product(
            ProductId id,
            MemberId sellerId,
            ProductName name,
            ProductDescription description,
            Money price,
            ProductStatus status,
            ProductCategory category,
            ProductImages productImages,
            boolean imagesLoaded
    ) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.category = category;
        this.productImages = productImages;
        this.imagesLoaded = imagesLoaded;
    }

    public static Product restore(RestoreProductCommand command) {
        return new Product(
                command.id(),
                command.sellerId(),
                command.name(),
                command.description(),
                command.price(),
                command.status(),
                command.category(),
                ProductImages.restore(command.thumbnailImage(), command.images()),
                command.imagesLoaded()
        );
    }

    public static Product create(CreateProductCommand command, MemberId sellerId, IdGenerator idGenerator) {
        return new Product(
                new ProductId(idGenerator.nextId()),
                sellerId,
                command.name(),
                command.description(),
                command.price(),
                ProductStatus.ON_SALE,
                command.category(),
                ProductImages.from(command.images(), idGenerator),
                true
        );
    }

    public void modify(ModifyProductCommand command) {
        validateNotDiscontinued("상품을 수정할 수 없는 상품입니다.");

        this.name = command.name();
        this.description = command.description();
        this.price = command.price();
        this.category = command.category();
    }

    public void validateOrderable() {
        validateNotDiscontinued("주문할 수 없는 상품입니다.");
    }

    public void discontinue() {
        validateNotDiscontinued("이미 단종된 상품입니다.");

        this.status = ProductStatus.DISCONTINUED;
    }

    public void validateSeller(MemberId requesterId) {
        if (this.sellerId.equals(requesterId)) {
            return;
        }

        throw new NotProductSellerException(id, requesterId, sellerId);
    }

    public ProductImage getThumbnailImage() {
        return productImages.getThumbnailImage();
    }

    public List<ProductImage> getImages() {
        return productImages.getImages();
    }

    public void addImages(List<AddProductImageCommand> addCommands, IdGenerator idGenerator) {
        validateNotDiscontinued("이미지를 추가할 수 없는 상품입니다.");
        validateImagesLoaded();

        this.productImages.add(addCommands, idGenerator);
    }

    public void removeImage(ProductImageId imageId) {
        validateNotDiscontinued("이미지를 제거할 수 없는 상품입니다.");
        validateImagesLoaded();

        this.productImages.removeImage(imageId);
    }

    public void changeThumbnailImage(ProductImageId imageId) {
        validateNotDiscontinued("대표 이미지를 변경할 수 없는 상품입니다.");
        validateImagesLoaded();

        this.productImages.changeThumbnailImage(imageId);
    }

    private void validateNotDiscontinued(String message) {
        if (this.status == ProductStatus.DISCONTINUED) {
            throw new InvalidValueException(message);
        }
    }

    private void validateImagesLoaded() {
        if (!this.imagesLoaded) {
            throw new AggregateNotLoadedException("이미지가 로딩되지 않은 상품입니다.");
        }
    }
}
