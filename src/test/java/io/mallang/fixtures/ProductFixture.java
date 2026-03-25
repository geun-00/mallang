package io.mallang.fixtures;

import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductImageCommand;
import io.mallang.product.domain.*;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.CreateProductImageCommand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProductFixture {

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
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                stockQuantity,
                "FOOD",
                List.of()
        );
    }

    public static CreateProductCommand generateProductCreateCommand(List<CreateProductImageCommand> images) {
        return new CreateProductCommand(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                images
        );
    }

    public static ModifyProductCommand generateModifyProductCommand() {
        return new ModifyProductCommand(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                "BOOKS"
        );
    }

    public static MemberId generateSellerId() {
        return new MemberId(UUID.randomUUID().toString());
    }

    public static Product generateProduct() {
        return generateProduct(generateProductStockQuantity());
    }

    public static Product generateProduct(int stockQuantity) {
        return Product.create(generateProductCreateCommand(stockQuantity), generateSellerId(), generateIdGenerator());
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
        images.add(new CreateProductImageCommand(generateProductImageUrl(), true));

        for (int i = 0; i < nonThumbnailCount; i++) {
            images.add(new CreateProductImageCommand(generateProductImageUrl(), false));
        }
        return images;
    }

    public static CreateProductCommand generateProductCreateCommandWithImages(int nonThumbnailCount) {
        return new CreateProductCommand(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                generateProductImageCommand(nonThumbnailCount)
        );
    }

    public static CreateProductCommand generateProductCreateCommandWithImages() {
        return new CreateProductCommand(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                List.of(
                        new CreateProductImageCommand(generateProductImageUrl(), true),
                        new CreateProductImageCommand(generateProductImageUrl(), false),
                        new CreateProductImageCommand(generateProductImageUrl(), false)
                )
        );
    }

    public static List<AddProductImageCommand> generateAddProductImageCommand(int count) {
        List<AddProductImageCommand> addImageCommands = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            addImageCommands.add(new AddProductImageCommand(generateProductImageUrl()));
        }

        return addImageCommands;
    }

    private static String generateProductName() {
        return "name" + UUID.randomUUID();
    }

    private static String generateProductDescription() {
        return "description" + UUID.randomUUID();
    }

    private static BigDecimal generateProductPriceAmount() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return BigDecimal.valueOf(random.nextInt(10000, 100000));
    }

    private static int generateProductStockQuantity() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(10, 100);
    }

    private static String generateProductImageUrl() {
        return "https://test.com/images/" + UUID.randomUUID();
    }
}
