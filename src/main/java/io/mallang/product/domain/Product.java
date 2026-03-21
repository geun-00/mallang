package io.mallang.product.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Money;
import lombok.Getter;

import java.util.List;

@Getter
public class Product {

    private final ProductId id;

    private final ProductImages productImages;

    private ProductName name;

    private ProductDescription description;

    private Money price;

    private StockQuantity stockQuantity;

    private ProductStatus status;

    private ProductCategory category;

    private Product(
            ProductId id,
            ProductName name,
            ProductDescription description,
            Money price,
            StockQuantity stockQuantity,
            ProductCategory category,
            ProductImages productImages
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = ProductStatus.of(stockQuantity);
        this.category = category;
        this.productImages = productImages;
    }

    public static Product create(ProductCreateCommand command, IdGenerator idGenerator) {
        return new Product(
                new ProductId(idGenerator.nextId()),
                new ProductName(command.name()),
                new ProductDescription(command.description()),
                new Money(command.price()),
                new StockQuantity(command.stockQuantity()),
                ProductCategory.valueOf(command.category()),
                ProductImages.from(command.images(), idGenerator)
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

    public ProductImage getThumbnailImage() {
        return productImages.getThumbnailImage();
    }

    public List<ProductImage> getImages() {
        return productImages.getImages();
    }

    public void addImages(List<AddProductImageCommand> addCommands, IdGenerator idGenerator) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("이미지를 추가할 수 없는 상품입니다.");

        this.productImages.add(addCommands, idGenerator);
    }

    public void removeImage(ProductImageId imageId) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("이미지를 제거할 수 없는 상품입니다.");

        this.productImages.removeImage(imageId);
    }

    public void changeThumbnailImage(ProductImageId imageId) {
        if (this.status == ProductStatus.DISCONTINUED)
            throw new IllegalStateException("대표 이미지를 변경할 수 없는 상품입니다.");

        this.productImages.changeThumbnailImage(imageId);
    }
}
