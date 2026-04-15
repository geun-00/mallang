package io.mallang.init;

import io.mallang.product.application.provided.command.RegisterProductUseCase;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.domain.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Component
@Profile("local")
@RequiredArgsConstructor
class ProductSeeder {

    record SellerProducts(String sellerId, List<String> productIds) {
    }

    private static final int PRODUCT_COUNT_PER_SELLER = 20;

    private final RegisterProductUseCase registerProductUseCase;

    private int productSequence = 1;

    List<SellerProducts> seed(List<String> sellerIds) {
        return sellerIds.stream()
                        .map(this::registerProducts)
                        .toList();
    }

    private SellerProducts registerProducts(String sellerId) {
        List<String> productIds = IntStream.range(0, PRODUCT_COUNT_PER_SELLER)
                                           .mapToObj(productIndex -> registerProduct(sellerId, productIndex))
                                           .toList();

        return new SellerProducts(sellerId, productIds);
    }

    private String registerProduct(String sellerId, int productIndex) {
        return registerProductUseCase.register(new RegisterProductCommand(
                                             sellerId,
                                             nextProductName(),
                                             "테스트 상품",
                                             randomPrice(),
                                             randomStockQuantity(),
                                             categoryOf(productIndex),
                                             List.of()
                                     ))
                                     .productId();
    }

    private String nextProductName() {
        return "테스트 상품 %03d".formatted(productSequence++);
    }

    private BigDecimal randomPrice() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int price = 10_000 + random.nextInt(900) * 100;

        return BigDecimal.valueOf(price);
    }

    private int randomStockQuantity() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        return random.nextInt(50, 100 + 1);
    }

    private String categoryOf(int productIndex) {
        ProductCategory[] categories = ProductCategory.values();

        return categories[productIndex % categories.length].name();
    }
}
