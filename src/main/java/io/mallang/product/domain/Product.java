package io.mallang.product.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.command.CreateProductCommand;
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

    private StockQuantity stockQuantity;

    private ProductStatus status;

    private ProductCategory category;

    private final boolean imagesLoaded;

    private Product(
            ProductId id,
            MemberId sellerId,
            ProductName name,
            ProductDescription description,
            Money price,
            StockQuantity stockQuantity,
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
        this.stockQuantity = stockQuantity;
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
                command.stockQuantity(),
                command.status(),
                command.category(),
                ProductImages.restore(command.thumbnailImage(), command.images()),
                command.imagesLoaded()
        );
    }

    public static Product create(CreateProductCommand command, MemberId sellerId, IdGenerator idGenerator) {
        StockQuantity stockQuantity = new StockQuantity(command.stockQuantity());

        return new Product(
                new ProductId(idGenerator.nextId()),
                sellerId,
                new ProductName(command.name()),
                new ProductDescription(command.description()),
                new Money(command.price()),
                stockQuantity,
                ProductStatus.of(stockQuantity),
                ProductCategory.valueOf(command.category()),
                ProductImages.from(command.images(), idGenerator),
                true
        );
    }

    public void addStock(int additionalStock) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("재고를 추가할 수 없는 상품입니다.");

        this.stockQuantity = this.stockQuantity.add(additionalStock);
        this.status = ProductStatus.of(stockQuantity);
    }

    public void deductStock(int deductedStock) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("재고를 차감할 수 없는 상품입니다.");

        this.stockQuantity = this.stockQuantity.deduct(deductedStock);
        this.status = ProductStatus.of(stockQuantity);
    }

    public void modify(ModifyProductCommand command) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("상품을 수정할 수 없는 상품입니다.");

        this.name = new ProductName(command.name());
        this.description = new ProductDescription(command.description());
        this.price = new Money(command.price());
        this.category = ProductCategory.valueOf(command.category());
    }

    public void discontinue() {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("이미 단종된 상품입니다.");

        this.status = ProductStatus.DISCONTINUED;
    }

    public boolean isSeller(MemberId memberId) {
        return this.sellerId.equals(memberId);
    }

    public ProductImage getThumbnailImage() {
        return productImages.getThumbnailImage();
    }

    public List<ProductImage> getImages() {
        return productImages.getImages();
    }

    public void addImages(List<AddProductImageCommand> addCommands, IdGenerator idGenerator) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("이미지를 추가할 수 없는 상품입니다.");
        if (!this.imagesLoaded)
            throw new IllegalStateException("이미지가 로딩되지 않은 상품입니다.");

        this.productImages.add(addCommands, idGenerator);
    }

    public void removeImage(ProductImageId imageId) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("이미지를 제거할 수 없는 상품입니다.");
        if (!this.imagesLoaded)
            throw new IllegalStateException("이미지가 로딩되지 않은 상품입니다.");

        this.productImages.removeImage(imageId);
    }

    public void changeThumbnailImage(ProductImageId imageId) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("대표 이미지를 변경할 수 없는 상품입니다.");
        if (!this.imagesLoaded)
            throw new IllegalStateException("이미지가 로딩되지 않은 상품입니다.");

        this.productImages.changeThumbnailImage(imageId);
    }
}
