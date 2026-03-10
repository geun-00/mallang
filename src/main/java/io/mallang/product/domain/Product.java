package io.mallang.product.domain;

import io.mallang.domain.common.IdGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.util.Assert.state;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    private ProductId id;

    private ProductName name;

    private ProductDescription description;

    private Money price;

    private StockQuantity stockQuantity;

    private ProductStatus status;

    private ProductCategory category;

    private ProductImages productImages;

    public static Product create(ProductCreateCommand createCommand, IdGenerator idGenerator) {
        Product product = new Product();

        product.id = new ProductId(idGenerator.nextId());
        product.name = new ProductName(createCommand.name());
        product.description = new ProductDescription(createCommand.description());
        product.price = new Money(BigDecimal.valueOf(createCommand.price()));

        StockQuantity stockQuantity = new StockQuantity(createCommand.stockQuantity());
        product.stockQuantity = stockQuantity;

        product.status = ProductStatus.of(stockQuantity);
        product.category = ProductCategory.valueOf(createCommand.category());
        product.productImages = ProductImages.from(createCommand, idGenerator);

        return product;
    }

    public void addStock(int additionalStock) {
        state(this.status != ProductStatus.DISCONTINUED, "재고를 추가할 수 없는 상품입니다.");

        this.stockQuantity = this.stockQuantity.add(additionalStock);
        this.status = ProductStatus.of(stockQuantity);
    }

    public void deductStock(int deductedStock) {
        state(this.status != ProductStatus.DISCONTINUED, "재고를 차감할 수 없는 상품입니다.");

        this.stockQuantity = this.stockQuantity.deduct(deductedStock);
        this.status = ProductStatus.of(stockQuantity);
    }

    public void modify(ModifyProductCommand modifyCommand) {
        state(this.status != ProductStatus.DISCONTINUED, "상품을 수정할 수 없는 상품입니다.");

        this.name = new ProductName(modifyCommand.name());
        this.description = new ProductDescription(modifyCommand.description());
        this.price = new Money(BigDecimal.valueOf(modifyCommand.price()));
        this.category = ProductCategory.valueOf(modifyCommand.category());
    }

    public void discontinue() {
        state(this.status != ProductStatus.DISCONTINUED, "이미 단종된 상품입니다.");

        this.status = ProductStatus.DISCONTINUED;
    }

    public ProductImage getThumbnailImage() {
        return productImages.getThumbnailImage();
    }

    public List<ProductImage> getImages() {
        return productImages.getImages();
    }

    public void addImages(List<AddProductImageCommand> addCommands, IdGenerator idGenerator) {
        state(this.status != ProductStatus.DISCONTINUED, "이미지를 추가할 수 없는 상품입니다.");
        this.productImages.add(addCommands, idGenerator);
    }
}
