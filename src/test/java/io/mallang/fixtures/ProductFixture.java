package io.mallang.fixtures;

import io.mallang.domain.common.IdGenerator;
import io.mallang.product.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ProductFixture {

    public static IdGenerator generateIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    public static ProductCreateCommand generateProductCreateCommand() {
        return generateProductCreateCommand(generateProductStockQuantity());
    }

    public static ProductCreateCommand generateProductCreateCommand(int stockQuantity) {
        return new ProductCreateCommand(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                stockQuantity,
                "FOOD",
                List.of()
        );
    }

    public static ProductCreateCommand generateProductCreateCommand(List<ProductImageCommand> images) {
        return new ProductCreateCommand(
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

    public static Product generateProduct() {
        return generateProduct(generateProductStockQuantity());
    }

    public static Product generateProduct(int stockQuantity) {
        return Product.create(generateProductCreateCommand(stockQuantity), generateIdGenerator());
    }

    public static Product generateProduct(List<ProductImageCommand> images) {
        return Product.create(generateProductCreateCommand(images), generateIdGenerator());
    }

    public static Product generateDiscontinuedProduct() {
        Product product = generateProduct();
        product.discontinue();

        return product;
    }

    public static ProductCreateCommand generateProductCreateCommandWithImages() {
        return new ProductCreateCommand(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                generateProductStockQuantity(),
                "FOOD",
                List.of(
                        new ProductImageCommand(generateProductImageUrl(), true),
                        new ProductImageCommand(generateProductImageUrl(), false),
                        new ProductImageCommand(generateProductImageUrl(), false)
                )
        );
    }

    private static String generateProductName() {
        return "name" + UUID.randomUUID();
    }

    private static String generateProductDescription() {
        return "description" + UUID.randomUUID();
    }

    private static int generateProductPriceAmount() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(10000, 100000);
    }

    private static int generateProductStockQuantity() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(10, 100);
    }

    private static String generateProductImageUrl() {
        return "https://test.com/images/" + UUID.randomUUID();
    }

    public static List<AddProductImageCommand> generateAddProductImageCommand(int count) {
        List<AddProductImageCommand> addImageCommands = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            addImageCommands.add(new AddProductImageCommand(generateProductImageUrl()));
        }

        return addImageCommands;
    }
}
