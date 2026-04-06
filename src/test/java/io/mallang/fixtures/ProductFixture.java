package io.mallang.fixtures;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.adapter.web.model.*;
import io.mallang.product.adapter.web.model.CreateProductRequest.ProductImageRequest;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductImageCommand;
import io.mallang.product.domain.*;
import io.mallang.product.domain.command.AddProductImageCommand;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.CreateProductImageCommand;
import io.mallang.product.domain.command.ModifyProductCommand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProductFixture {

    public static UpdateProductRequest generateUpdateProductRequest() {
        return new UpdateProductRequest(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                "BOOKS"
        );
    }

    public static AddStockRequest generateAddStockRequest() {
        return new AddStockRequest(generateProductStockQuantity());
    }

    public static AddProductImagesRequest generateAddProductImagesRequest() {
        return new AddProductImagesRequest(List.of(
                generateProductImageUrl(),
                generateProductImageUrl()
        ));
    }

    public static CreateProductRequest generateCreateProductRequest() {
        return new CreateProductRequest(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                null
        );
    }

    public static CreateProductRequest generateCreateProductRequestWithImages() {
        return new CreateProductRequest(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                List.of(
                        new ProductImageRequest(generateProductImageUrl(), true),
                        new ProductImageRequest(generateProductImageUrl(), false)
                )
        );
    }

    public static RegisterProductCommand generateRegisterProductCommand() {
        return generateRegisterProductCommand(List.of());
    }

    public static RegisterProductCommand generateRegisterProductCommandWithImages() {
        return generateRegisterProductCommand(List.of(
                new RegisterProductImageCommand("https://test.com/images/" + UUID.randomUUID(), true),
                new RegisterProductImageCommand("https://test.com/images/" + UUID.randomUUID(), false)
        ));
    }

    public static RegisterProductCommand generateRegisterProductCommand(List<RegisterProductImageCommand> images) {
        return new RegisterProductCommand(
                generateSellerId().value(),
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                images
        );
    }

    public static IdGenerator generateIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    public static CreateProductCommand generateProductCreateCommand() {
        return generateProductCreateCommand(generateProductStockQuantity());
    }

    public static CreateProductCommand generateProductCreateCommand(int stockQuantity) {
        return new CreateProductCommand(
                new ProductName(generateProductName()),
                new ProductDescription(generateProductDescription()),
                new Money(generateProductPriceAmount()),
                new StockQuantity(stockQuantity),
                ProductCategory.FOOD,
                List.of()
        );
    }

    public static CreateProductCommand generateProductCreateCommand(List<CreateProductImageCommand> images) {
        return new CreateProductCommand(
                new ProductName(generateProductName()),
                new ProductDescription(generateProductDescription()),
                new Money(generateProductPriceAmount()),
                new StockQuantity(generateProductStockQuantity()),
                ProductCategory.FOOD,
                images
        );
    }

    public static ModifyProductCommand generateModifyProductCommand() {
        return new ModifyProductCommand(
                new ProductName(generateProductName()),
                new ProductDescription(generateProductDescription()),
                new Money(generateProductPriceAmount()),
                ProductCategory.BOOKS
        );
    }

    public static MemberId generateSellerId() {
        return new MemberId(UUID.randomUUID().toString());
    }

    public static Product generateProduct() {
        return generateProduct(generateProductStockQuantity());
    }

    public static Product generateProduct(int stockQuantity) {
        return generateProduct(generateSellerId(), stockQuantity);
    }

    public static Product generateProduct(MemberId sellerId, int stockQuantity) {
        return Product.create(generateProductCreateCommand(stockQuantity), sellerId, generateIdGenerator());
    }

    public static Product generateDiscontinuedProduct() {
        Product product = generateProduct();
        product.discontinue();

        return product;
    }

    public static Product generateProductWithImages() {
        return Product.create(generateProductCreateCommandWithImages(), generateSellerId(), generateIdGenerator());
    }

    public static Product generateProductWithImages(int nonThumbnailCount) {
        List<CreateProductImageCommand> images = generateProductImageCommand(nonThumbnailCount);

        return Product.create(generateProductCreateCommand(images), generateSellerId(), generateIdGenerator());
    }

    private static List<CreateProductImageCommand> generateProductImageCommand(int nonThumbnailCount) {
        List<CreateProductImageCommand> images = new ArrayList<>();
        images.add(new CreateProductImageCommand(new ImageUrl(generateProductImageUrl()), true));

        for (int i = 0; i < nonThumbnailCount; i++) {
            images.add(new CreateProductImageCommand(new ImageUrl(generateProductImageUrl()), false));
        }
        return images;
    }

    public static CreateProductCommand generateProductCreateCommandWithImages(int nonThumbnailCount) {
        return new CreateProductCommand(
                new ProductName(generateProductName()),
                new ProductDescription(generateProductDescription()),
                new Money(generateProductPriceAmount()),
                new StockQuantity(generateProductStockQuantity()),
                ProductCategory.FOOD,
                generateProductImageCommand(nonThumbnailCount)
        );
    }

    public static CreateProductCommand generateProductCreateCommandWithImages() {
        return new CreateProductCommand(
                new ProductName(generateProductName()),
                new ProductDescription(generateProductDescription()),
                new Money(generateProductPriceAmount()),
                new StockQuantity(generateProductStockQuantity()),
                ProductCategory.FOOD,
                List.of(
                        new CreateProductImageCommand(new ImageUrl(generateProductImageUrl()), true),
                        new CreateProductImageCommand(new ImageUrl(generateProductImageUrl()), false),
                        new CreateProductImageCommand(new ImageUrl(generateProductImageUrl()), false)
                )
        );
    }

    public static List<AddProductImageCommand> generateAddProductImageCommand(int count) {
        List<AddProductImageCommand> addImageCommands = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            addImageCommands.add(new AddProductImageCommand(new ImageUrl(generateProductImageUrl())));
        }

        return addImageCommands;
    }

    public static String generateProductName() {
        return "name" + UUID.randomUUID();
    }

    public static String generateProductDescription() {
        return "description" + UUID.randomUUID();
    }

    public static BigDecimal generateProductPriceAmount() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return BigDecimal.valueOf(random.nextInt(10000, 100000));
    }

    public static int generateProductStockQuantity() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(10, 100);
    }

    public static String generateProductImageUrl() {
        return "https://test.com/images/" + UUID.randomUUID();
    }
}
