package io.mallang.assertions;

import io.mallang.member.domain.Member;
import io.mallang.product.application.provided.command.model.UpdateProductCommand;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.ProductDescription;
import io.mallang.product.domain.ProductName;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.ModifyProductCommand;
import org.assertj.core.api.ThrowingConsumer;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static io.mallang.product.application.provided.query.model.ProductDetailView.ProductImageView;
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

    public static ThrowingConsumer<ProductDetailView> isDerivedFrom(Product product, Member seller) {
        return view -> {
            assertThat(view.productId()).isEqualTo(product.getId().value());
            assertThat(view.sellerIdValue()).isEqualTo(seller.getId().value());
            assertThat(view.sellerNickname()).isEqualTo(seller.getNickname().value());
            assertThat(view.name()).isEqualTo(product.getName().value());
            assertThat(view.description()).isEqualTo(product.getDescription().value());
            assertThat(view.price()).matches(priceEquals(product.getPrice().value()));
            assertThat(view.stockQuantity()).isEqualTo(product.getStockQuantity().value());
            assertThat(view.status()).isEqualTo(product.getStatus().name());
            assertThat(view.category()).isEqualTo(product.getCategory().name());
            assertThat(view.thumbnailImageUrl()).isEqualTo(product.getThumbnailImage().imageUrl().value());
            assertThat(view.images()).extracting(ProductImageView::imageUrl)
                                     .containsAll(product.getImages()
                                                         .stream()
                                                         .map(pi -> pi.imageUrl().value())
                                                         .toList());
        };
    }

    private static Predicate<? super BigDecimal> priceEquals(BigDecimal expected) {
        return actual -> actual.compareTo(expected) == 0;
    }
}
