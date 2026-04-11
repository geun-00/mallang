package io.mallang.test.product.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.product.adapter.web.ProductCommandApi;
import io.mallang.product.adapter.web.model.AddStockRequest;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.adapter.web.model.DeductStockRequest;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import io.mallang.product.application.provided.command.*;
import io.mallang.test.support.security.WithMockMember;
import io.mallang.test.support.web.WebMvcRequestTestSupport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.ArrayList;
import java.util.List;

import static io.mallang.fixtures.ProductFixture.*;
import static io.mallang.fixtures.api.ApiFixture.PRODUCTS_API;
import static io.mallang.product.adapter.web.model.CreateProductRequest.ProductImageRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@WebMvcAdapterTest(ProductCommandApi.class)
class ProductCommandApiWebMvcTest extends WebMvcRequestTestSupport {

    @MockitoBean
    AddStockUseCase addStockUseCase;

    @MockitoBean
    DeductStockUseCase deductStockUseCase;

    @MockitoBean
    UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    RegisterProductUseCase registerProductUseCase;

    @MockitoBean
    DiscontinueProductUseCase discontinueProductUseCase;

    @Nested
    class 상품_등록_요청_검증 {

        @WithMockMember
        @Test
        void images_요소가_null이면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            List<ProductImageRequest> images = new ArrayList<>();
            images.add(new ProductImageRequest(generateProductImageUrl(), true));
            images.add(null);

            var request = new CreateProductRequest(
                    generateProductName(),
                    generateProductDescription(),
                    generateProductPriceAmount(),
                    generateProductStockQuantity(),
                    "FOOD",
                    images
            );

            // when
            MvcTestResult result = postJson(PRODUCTS_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " "})
        void images_요소의_imageUrl이_null_또는_비어있으면_400_Bad_Request_상태코드를_반환한다(String invalidImageUrl) throws JsonProcessingException {
            // given
            var request = new CreateProductRequest(
                    generateProductName(),
                    generateProductDescription(),
                    generateProductPriceAmount(),
                    generateProductStockQuantity(),
                    "FOOD",
                    List.of(
                            new ProductImageRequest(invalidImageUrl, true),
                            new ProductImageRequest(generateProductImageUrl(), false)
                    )
            );

            // when
            MvcTestResult result = postJson(PRODUCTS_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void name_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String invalidName) throws JsonProcessingException {
            // given
            var request = new CreateProductRequest(
                    invalidName,
                    generateProductDescription(),
                    generateProductPriceAmount(),
                    generateProductStockQuantity(),
                    "FOOD",
                    List.of()
            );

            // when
            MvcTestResult result = postJson(PRODUCTS_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void description_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new CreateProductRequest(
                    generateProductName(),
                    null,
                    generateProductPriceAmount(),
                    generateProductStockQuantity(),
                    "FOOD",
                    List.of()
            );

            // when
            MvcTestResult result = postJson(PRODUCTS_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void price_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new CreateProductRequest(
                    generateProductName(),
                    generateProductDescription(),
                    null,
                    generateProductStockQuantity(),
                    "FOOD",
                    List.of()
            );

            // when
            MvcTestResult result = postJson(PRODUCTS_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void category_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String invalidCategory) throws JsonProcessingException {
            // given
            var request = new CreateProductRequest(
                    generateProductName(),
                    generateProductDescription(),
                    generateProductPriceAmount(),
                    generateProductStockQuantity(),
                    invalidCategory,
                    List.of()
            );

            // when
            MvcTestResult result = postJson(PRODUCTS_API, request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }

    @Nested
    class 상품_수정_요청_검증 {

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void name_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String invalidName) throws JsonProcessingException {
            // given
            var request = new UpdateProductRequest(invalidName, generateProductDescription(), generateProductPriceAmount(), "BOOKS");

            // when
            MvcTestResult result = putJson(PRODUCTS_API + "/" + "product-id", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void description_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new UpdateProductRequest(generateProductName(), null, generateProductPriceAmount(), "BOOKS");

            // when
            MvcTestResult result = putJson(PRODUCTS_API + "/" + "product-id", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void price_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new UpdateProductRequest(generateProductName(), generateProductDescription(), null, "BOOKS");

            // when
            MvcTestResult result = putJson(PRODUCTS_API + "/" + "product-id", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void category_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String invalidCategory) throws JsonProcessingException {
            // given
            var request = new UpdateProductRequest(generateProductName(), generateProductDescription(), generateProductPriceAmount(), invalidCategory);

            // when
            MvcTestResult result = putJson(PRODUCTS_API + "/" + "product-id", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }

    @Nested
    class 재고_추가_요청_검증 {

        @WithMockMember
        @Test
        void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new AddStockRequest(null);

            // when
            MvcTestResult result = patchJson(PRODUCTS_API + "/" + "product-id" + "/stock/add", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void quantity가_양수가_아니면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new AddStockRequest(-1);

            // when
            MvcTestResult result = patchJson(PRODUCTS_API + "/" + "product-id" + "/stock/add", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }

    @Nested
    class 재고_차감_요청_검증 {

        @WithMockMember
        @Test
        void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new DeductStockRequest(null);

            // when
            MvcTestResult result = patchJson(PRODUCTS_API + "/" + "product-id" + "/stock/deduct", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
