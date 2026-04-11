package io.mallang.test.product.adapter.web;

import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.product.adapter.web.ProductQueryApi;
import io.mallang.product.application.provided.query.GetProductDetailUseCase;
import io.mallang.product.application.provided.query.SearchProductsUseCase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static io.mallang.fixtures.api.ApiFixture.PRODUCTS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@WebMvcAdapterTest(ProductQueryApi.class)
class ProductQueryApiWebMvcTest {

    @MockitoBean
    SearchProductsUseCase searchProductsUseCase;

    @MockitoBean
    GetProductDetailUseCase getProductDetailUseCase;

    @Nested
    class 상품_목록_조회_요청_검증 {

        @ParameterizedTest
        @ValueSource(strings = {"minPrice", "maxPrice"})
        void 가격_조건이_음수면_400_Bad_Request_상태코드를_반환한다(String priceParameter, @Autowired MockMvcTester client) {
            // when
            MvcTestResult result = client.get().uri(PRODUCTS_API).param(priceParameter, "-1").exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
