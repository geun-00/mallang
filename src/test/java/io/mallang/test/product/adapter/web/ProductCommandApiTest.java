package io.mallang.test.product.adapter.web;

import io.mallang.fixtures.api.FixtureSession;
import io.mallang.fixtures.api.FixtureSessionFactory;
import io.mallang.annotations.WebAdapterTest;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static io.mallang.fixtures.ProductFixture.*;
import static io.mallang.fixtures.api.ApiFixture.PRODUCTS_API;
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
            void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.product().registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.product().registerProduct(request);

                URI location = response.getHeaders().getLocation();
                assertThat(location).isNotNull();
                assertThat(location.getPath()).startsWith(PRODUCTS_API + "/");
                assertThat(location.getPath().replace(PRODUCTS_API + "/", "")).isNotBlank();
            }

            @Test
            void 올바르게_요청하면_Location_헤더의_식별자로_상품을_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.auth().createMemberThenLogin();

                String productIdValue = fixture.product().registerProductThenGetId();

                assertThatCode(() -> loadProductPort.getById(new ProductId(productIdValue))).doesNotThrowAnyException();
            }

            @Test
            void 이미지_없이_등록할_수_있다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.product().registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 이미지와_함께_등록할_수_있다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = generateCreateProductRequestWithImages();

                ResponseEntity<Void> response = fixture.product().registerProduct(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                var request = generateCreateProductRequest();

                ResponseEntity<Void> response = fixture.product()
                                                       .unauthenticatedClient()
                                                       .postForEntity(PRODUCTS_API, request, Void.class);

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = new CreateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        generateProductStockQuantity(),
                        "invalid-Category",
                        List.of()
                );

                ResponseEntity<Void> response = fixture.product().registerProduct(request);

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
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = fixture.product().updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = fixture.product()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.put(PRODUCTS_API + "/" + productId)
                                                                            .body(request),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = fixture.product().updateProduct("non-existent-product-id", request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = new UpdateProductRequest(
                        generateProductName(),
                        generateProductDescription(),
                        generateProductPriceAmount(),
                        "invalid-Category"
                );

                ResponseEntity<Void> response = fixture.product().updateProduct(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired FixtureSessionFactory fixtureSessionFactory
            ) {
                FixtureSession anotherFixture = fixtureSessionFactory.create();

                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                anotherFixture.auth().createMemberThenLogin();
                var request = generateUpdateProductRequest();

                ResponseEntity<Void> response = anotherFixture.product().updateProduct(productId, request);

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
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                ResponseEntity<Void> response = fixture.product().discontinue(productId);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                ResponseEntity<Void> response = fixture.product()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.patch(PRODUCTS_API + "/" + productId + "/discontinue")
                                                                            .build(),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = fixture.product().discontinue("non-existent-product-id");

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().discontinue(productId);

                ResponseEntity<Void> response = fixture.product().discontinue(productId);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired FixtureSessionFactory fixtureSessionFactory
            ) {
                FixtureSession anotherFixture = fixtureSessionFactory.create();

                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                anotherFixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = anotherFixture.product().discontinue(productId);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }
}
