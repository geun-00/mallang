package io.mallang.test.product.adapter.web.mapper;

import io.mallang.annotations.MapperTest;
import io.mallang.common.applicaiton.query.SliceResult;
import io.mallang.assertions.ProductAssertions;
import io.mallang.product.adapter.web.mapper.ProductResponseMapper;
import io.mallang.product.adapter.web.model.ProductDetailResponse;
import io.mallang.product.adapter.web.model.SearchProductsResponse;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.application.provided.query.model.ProductListView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MapperTest
@DisplayName("ProductResponse Mapper")
class ProductResponseMapperTest {

    @Test
    void 검색_결과를_검색_응답으로_변환할_수_있다() {
        SliceResult<ProductListView> result = new SliceResult<>(
                List.of(
                        new ProductListView(
                                "product-1",
                                "seller-1",
                                "sellerA",
                                "Apple",
                                BigDecimal.valueOf(3000),
                                10,
                                "ON_SALE",
                                "FOOD",
                                "https://example.com/apple.jpg"
                        )
                ),
                true,
                "product-1"
        );

        SearchProductsResponse response = ProductResponseMapper.toSearchProductsResponse(result);

        assertThat(response).satisfies(ProductAssertions.isMappedFrom(result));
    }

    @Test
    void 상품_상세_뷰를_상세_응답으로_변환할_수_있다() {
        ProductDetailView view = new ProductDetailView(
                "product-1",
                "seller-1",
                "sellerA",
                "Apple",
                "Fresh apple",
                BigDecimal.valueOf(3000),
                10,
                "ON_SALE",
                "FOOD",
                "https://example.com/thumb.jpg",
                List.of(
                        new ProductDetailView.ProductImageView(
                                "image-1",
                                "https://example.com/thumb.jpg",
                                true
                        ),
                        new ProductDetailView.ProductImageView(
                                "image-2",
                                "https://example.com/detail.jpg",
                                false
                        )
                )
        );

        ProductDetailResponse response = ProductResponseMapper.toProductDetailResponse(view);

        assertThat(response).satisfies(ProductAssertions.isMappedFrom(view));
    }
}
