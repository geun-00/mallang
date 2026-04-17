package io.mallang.assertions;

import io.mallang.common.application.query.SliceResult;
import io.mallang.member.domain.Member;
import io.mallang.product.adapter.web.model.ProductDetailResponse;
import io.mallang.product.adapter.web.model.SearchProductsResponse;
import io.mallang.product.application.provided.command.model.UpdateProductCommand;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.ProductDescription;
import io.mallang.product.domain.ProductName;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.ModifyProductCommand;
import io.mallang.stock.domain.Stock;
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

    public static ThrowingConsumer<ProductDetailView> isDerivedFrom(Product product, Member seller, Stock stock) {
        return view -> {
            assertThat(view.productId()).isEqualTo(product.getId().value());
            assertThat(view.sellerIdValue()).isEqualTo(seller.getId().value());
            assertThat(view.sellerNickname()).isEqualTo(seller.getNickname().value());
            assertThat(view.name()).isEqualTo(product.getName().value());
            assertThat(view.description()).isEqualTo(product.getDescription().value());
            assertThat(view.price()).matches(priceEquals(product.getPrice().value()));
            assertThat(view.stockQuantity()).isEqualTo(stock.getQuantity().value());
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

    public static ThrowingConsumer<SearchProductsResponse> isMappedFrom(SliceResult<ProductListView> result) {
        return response -> {
            assertThat(response.items()).hasSize(result.items().size());

            for (int i = 0; i < result.items().size(); i++) {
                ProductListView item = result.items().get(i);
                SearchProductsResponse.ProductSummary summary = response.items().get(i);

                assertThat(summary.productId()).isEqualTo(item.productId());
                assertThat(summary.sellerNickname()).isEqualTo(item.sellerNickname());
                assertThat(summary.name()).isEqualTo(item.name());
                assertThat(summary.price()).isEqualTo(item.price());
                assertThat(summary.stockQuantity()).isEqualTo(item.stockQuantity());
                assertThat(summary.status()).isEqualTo(item.status());
                assertThat(summary.category()).isEqualTo(item.category());
                assertThat(summary.thumbnailImageUrl()).isEqualTo(item.thumbnailImageUrl());
            }

            assertThat(response.hasNext()).isEqualTo(result.hasNext());
            assertThat(response.nextCursor()).isEqualTo(result.nextCursor());
        };
    }

    public static ThrowingConsumer<ProductDetailResponse> isMappedFrom(ProductDetailView view) {
        return response -> {
            assertThat(response.productId()).isEqualTo(view.productId());
            assertThat(response.sellerIdValue()).isEqualTo(view.sellerIdValue());
            assertThat(response.sellerNickname()).isEqualTo(view.sellerNickname());
            assertThat(response.name()).isEqualTo(view.name());
            assertThat(response.description()).isEqualTo(view.description());
            assertThat(response.price()).isEqualTo(view.price());
            assertThat(response.stockQuantity()).isEqualTo(view.stockQuantity());
            assertThat(response.status()).isEqualTo(view.status());
            assertThat(response.category()).isEqualTo(view.category());
            assertThat(response.thumbnailImageUrl()).isEqualTo(view.thumbnailImageUrl());
            assertThat(response.images()).hasSize(view.images().size());

            for (int i = 0; i < view.images().size(); i++) {
                ProductImageView expected = view.images().get(i);
                ProductDetailResponse.ProductImageResponse actual = response.images().get(i);

                assertThat(actual.imageId()).isEqualTo(expected.imageId());
                assertThat(actual.imageUrl()).isEqualTo(expected.imageUrl());
                assertThat(actual.thumbnail()).isEqualTo(expected.thumbnail());
            }
        };
    }

    private static Predicate<? super BigDecimal> priceEquals(BigDecimal expected) {
        return actual -> actual.compareTo(expected) == 0;
    }
}
