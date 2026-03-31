package io.mallang.assertions;

import io.mallang.product.domain.*;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.application.provided.command.model.UpdateProductCommand;
import io.mallang.product.domain.command.ModifyProductCommand;
import org.assertj.core.api.ThrowingConsumer;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductAssertions {

    public static ThrowingConsumer<Product> isDerivedFrom(CreateProductCommand command) {
        return product -> {
            assertThat(product.getName()).isEqualTo(command.name());
            assertThat(product.getDescription()).isEqualTo(command.description());
            assertThat(product.getPrice().value()).matches(priceEquals(command.price().value()));
            assertThat(product.getStockQuantity()).isEqualTo(command.stockQuantity());
            assertThat(product.getCategory()).isEqualTo(command.category());
        };
    }

    public static ThrowingConsumer<Product> isDerivedFrom(ModifyProductCommand command) {
        return product -> {
            assertThat(product.getName()).isEqualTo(command.name());
            assertThat(product.getDescription()).isEqualTo(command.description());
            assertThat(product.getPrice().value()).matches(priceEquals(command.price().value()));
            assertThat(product.getCategory()).isEqualTo(command.category());
        };
    }

    public static ThrowingConsumer<Product> isDerivedFrom(UpdateProductCommand command) {
        return product -> {
            assertThat(product.getName()).isEqualTo(new ProductName(command.name()));
            assertThat(product.getDescription()).isEqualTo(new ProductDescription(command.description()));
            assertThat(product.getPrice().value()).matches(priceEquals(command.price()));
            assertThat(product.getCategory()).isEqualTo(ProductCategory.valueOf(command.category()));
        };
    }

    public static ThrowingConsumer<Product> isSameAs(Product expected) {
        return actual -> {
            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getSellerId()).isEqualTo(expected.getSellerId());
            assertThat(actual.getName()).isEqualTo(expected.getName());
            assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
            assertThat(actual.getPrice().value()).matches(priceEquals(expected.getPrice().value()));
            assertThat(actual.getStockQuantity()).isEqualTo(expected.getStockQuantity());
            assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
            assertThat(actual.getCategory()).isEqualTo(expected.getCategory());
        };
    }

    public static ThrowingConsumer<Product> isSameAsWithImages(Product expected) {
        return actual -> {
            isSameAs(expected).accept(actual);
            assertThat(actual.getThumbnailImage()).isEqualTo(expected.getThumbnailImage());
            assertThat(actual.getImages()).isEqualTo(expected.getImages());
        };
    }

    private static Predicate<? super BigDecimal> priceEquals(BigDecimal expected) {
        return actual -> actual.compareTo(expected) == 0;
    }
}
