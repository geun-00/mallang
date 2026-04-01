package io.mallang.test.product.adapter.web;

import io.mallang.TestFixture;
import io.mallang.WebAdapterTest;
import io.mallang.product.adapter.web.model.AddStockRequest;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.adapter.web.model.DeductStockRequest;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static io.mallang.fixtures.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.http.HttpStatus.*;

@WebAdapterTest
@DisplayName("ProductCommand API")
class ProductCommandApiTest {

    @Nested
    @DisplayName("POST /products")
    class 상품_등록 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.registerProduct(request);

                URI location = response.getHeaders().getLocation();
                assertThat(location).isNotNull();
                assertThat(location.getPath()).startsWith("/products/");
                assertThat(location.getPath().replace("/products/", "")).isNotBlank();
            }

            @Test
            void 올바르게_요청하면_Location_헤더의_식별자로_상품을_조회할_수_있다(
                    @Autowired TestFixture fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.createMemberThenLogin();
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.registerProduct(request);

                String productIdValue = response.getHeaders().getLocation().getPath().substring("/products/".length());
                assertThatCode(() -> loadProductPort.getById(new ProductId(productIdValue)))
                        .doesNotThrowAnyException();
            }

            @Test
            void 이미지_없이_등록할_수_있다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 이미지와_함께_등록할_수_있다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = generateCreateProductRequestWithImages();

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.unauthenticatedClient().postForEntity(
                        "/products",
                        request,
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @Test
            void images_요소가_null이면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                List<CreateProductRequest.ProductImageRequest> images = new ArrayList<>();
                images.add(new CreateProductRequest.ProductImageRequest(generateProductImageUrl(), true));
                images.add(null);
                var request = new CreateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        generateProductStockQuantity(),
                        "FOOD",
                        images
                );

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {"", " "})
            void images_요소의_imageUrl이_null_또는_비어있으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidImageUrl,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new CreateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        generateProductStockQuantity(),
                        "FOOD",
                        List.of(
                                new CreateProductRequest.ProductImageRequest(invalidImageUrl, true),
                                new CreateProductRequest.ProductImageRequest(generateProductImageUrl(), false)
                        )
                );

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void name_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidName,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new CreateProductRequest(
                        invalidName,
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        generateProductStockQuantity(),
                        "FOOD",
                        List.of()
                );

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void description_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new CreateProductRequest(
                        generateProductName(),
                        null,
                        generateProductPriceAmount(),
                        generateProductStockQuantity(),
                        "FOOD",
                        List.of()
                );

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void price_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new CreateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        null,
                        generateProductStockQuantity(),
                        "FOOD",
                        List.of()
                );

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void category_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidCategory,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new CreateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        generateProductStockQuantity(),
                        invalidCategory,
                        List.of()
                );

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = new CreateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        generateProductStockQuantity(),
                        "invalid-Category",
                        List.of()
                );

                ResponseEntity<Void> response = fixture.registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }

    @Nested
    @DisplayName("PUT /products/{productId}")
    class 상품_수정 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = fixture.updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                        RequestEntity.put("/products/" + productId).body(request),
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void name_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidName,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new UpdateProductRequest(
                        invalidName,
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        "BOOKS"
                );

                ResponseEntity<Void> response = fixture.updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void description_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new UpdateProductRequest(
                        generateProductName(),
                        null,
                        generateProductPriceAmount(),
                        "BOOKS"
                );

                ResponseEntity<Void> response = fixture.updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void price_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new UpdateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        null,
                        "BOOKS"
                );

                ResponseEntity<Void> response = fixture.updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void category_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidCategory,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new UpdateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        invalidCategory
                );

                ResponseEntity<Void> response = fixture.updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = fixture.updateProduct("non-existent-product-id", request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new UpdateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        "invalid-Category"
                );

                ResponseEntity<Void> response = fixture.updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired TestFixture ownerFixture,
                    @Autowired TestFixture anotherFixture
            ) {
                ownerFixture.createMemberThenLogin();
                String productId = ownerFixture.registerProductThenGetId();

                anotherFixture.createMemberThenLogin();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = anotherFixture.updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /products/{productId}/stock/add")
    class 재고_추가 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = fixture.addStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                        RequestEntity.patch("/products/" + productId + "/stock/add").body(request),
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @Test
            void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new AddStockRequest(null);

                ResponseEntity<Void> response = fixture.addStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void quantity가_양수가_아니면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new AddStockRequest(-1);

                ResponseEntity<Void> response = fixture.addStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = fixture.addStock("non-existent-product-id", request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired TestFixture ownerFixture,
                    @Autowired TestFixture anotherFixture
            ) {
                ownerFixture.createMemberThenLogin();
                String productId = ownerFixture.registerProductThenGetId();

                anotherFixture.createMemberThenLogin();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = anotherFixture.addStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /products/{productId}/stock/deduct")
    class 재고_차감 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                fixture.addStock(productId, new AddStockRequest(50));
                var request = generateDeductStockRequest(5);

                ResponseEntity<Void> response = fixture.deductStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = generateDeductStockRequest(1);

                ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                        RequestEntity.patch("/products/" + productId + "/stock/deduct").body(request),
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @Test
            void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                var request = new DeductStockRequest(null);

                ResponseEntity<Void> response = fixture.deductStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                var request = generateDeductStockRequest(1);

                ResponseEntity<Void> response = fixture.deductStock("non-existent-product-id", request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();

                var request = generateDeductStockRequest(Integer.MAX_VALUE);

                ResponseEntity<Void> response = fixture.deductStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired TestFixture ownerFixture,
                    @Autowired TestFixture anotherFixture
            ) {
                ownerFixture.createMemberThenLogin();
                String productId = ownerFixture.registerProductThenGetId();
                ownerFixture.addStock(productId, new AddStockRequest(50));

                anotherFixture.createMemberThenLogin();
                var request = generateDeductStockRequest(5);

                ResponseEntity<Void> response = anotherFixture.deductStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /products/{productId}/discontinue")
    class 판매_중단 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();

                ResponseEntity<Void> response = fixture.discontinue(productId);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();

                ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                        RequestEntity.patch("/products/" + productId + "/discontinue").build(),
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();

                ResponseEntity<Void> response = fixture.discontinue("non-existent-product-id");

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
                fixture.createMemberThenLogin();
                String productId = fixture.registerProductThenGetId();
                fixture.discontinue(productId);

                ResponseEntity<Void> response = fixture.discontinue(productId);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired TestFixture ownerFixture,
                    @Autowired TestFixture anotherFixture
            ) {
                ownerFixture.createMemberThenLogin();
                String productId = ownerFixture.registerProductThenGetId();

                anotherFixture.createMemberThenLogin();

                ResponseEntity<Void> response = anotherFixture.discontinue(productId);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }
}
