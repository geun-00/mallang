package io.mallang;

import io.mallang.domain.common.Money;
import io.mallang.product.domain.*;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductAssertions {

    public static ThrowingConsumer<Product> isDerivedFrom(ProductCreateCommand command) {
        return product -> {
            assertThat(product.getName()).isEqualTo(new ProductName(command.name()));
            assertThat(product.getDescription()).isEqualTo(new ProductDescription(command.description()));
            assertThat(product.getPrice()).isEqualTo(new Money(command.price()));
            assertThat(product.getStockQuantity()).isEqualTo(new StockQuantity(command.stockQuantity()));
            assertThat(product.getCategory()).isEqualTo(ProductCategory.valueOf(command.category()));
        };
    }

    public static ThrowingConsumer<Product> isDerivedFrom(ModifyProductCommand command) {
        return product -> {
            assertThat(product.getName()).isEqualTo(new ProductName(command.name()));
            assertThat(product.getDescription()).isEqualTo(new ProductDescription(command.description()));
            assertThat(product.getPrice()).isEqualTo(new Money(command.price()));
            assertThat(product.getCategory()).isEqualTo(ProductCategory.valueOf(command.category()));
        };
    }
}
