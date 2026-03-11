package io.mallang;

import io.mallang.product.domain.ModifyProductCommand;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductCreateCommand;
import org.assertj.core.api.ThrowingConsumer;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductAssertions {

    public static ThrowingConsumer<Product> isDerivedFrom(ProductCreateCommand createCommand) {
        return product -> {
            assertThat(product.getName().value()).isEqualTo(createCommand.name());
            assertThat(product.getDescription().value()).isEqualTo(createCommand.description());
            assertThat(product.getPrice().value()).matches(priceEqual(createCommand.price()));
            assertThat(product.getStockQuantity().value()).isEqualTo(createCommand.stockQuantity());
            assertThat(product.getCategory().name()).isEqualTo(createCommand.category());
        };
    }

    public static ThrowingConsumer<Product> isDerivedFrom(ModifyProductCommand modifyCommand) {
        return product -> {
            assertThat(product.getName().value()).isEqualTo(modifyCommand.name());
            assertThat(product.getDescription().value()).isEqualTo(modifyCommand.description());
            assertThat(product.getPrice().value()).matches(priceEqual(modifyCommand.price()));
            assertThat(product.getCategory().name()).isEqualTo(modifyCommand.category());
        };
    }

    private static Predicate<BigDecimal> priceEqual(int price) {
        return actual -> actual.compareTo(BigDecimal.valueOf(price)) == 0;
    }
}
