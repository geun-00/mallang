package io.mallang.test.product.adapter.web;

import io.mallang.FixtureSession;
import io.mallang.FixtureSessionFactory;
import io.mallang.annotations.WebAdapterTest;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
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

import java.util.ArrayList;
import java.util.List;

import static io.mallang.fixtures.ProductFixture.generateAddProductImagesRequest;
import static io.mallang.fixtures.ProductFixture.generateProductImageUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@WebAdapterTest
@DisplayName("ProductImageCommand API")
class ProductImageCommandApiTest {

    @Nested
    @DisplayName("POST /products/{productId}/images")
    class 상품_이미지_추가 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = generateAddProductImagesRequest();

                ResponseEntity<Void> response = fixture.product().addImages(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = generateAddProductImagesRequest();

                ResponseEntity<Void> response = fixture.product()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.post("/products/" + productId + "/images")
                                                                            .body(request),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @Test
            void imageUrls_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = new AddProductImagesRequest(null);

                ResponseEntity<Void> response = fixture.product().addImages(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {"", "   "})
            void imageUrls_요소가_null_또는_비어있는_문자열이면_400_Bad_Request_상태코드를_반환한다(
                    String invalidImageUrl,
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                List<String> imageUrls = new ArrayList<>();
                imageUrls.add(generateProductImageUrl());
                imageUrls.add(invalidImageUrl);

                var request = new AddProductImagesRequest(imageUrls);

                ResponseEntity<Void> response = fixture.product().addImages(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = generateAddProductImagesRequest();

                ResponseEntity<Void> response = fixture.product().addImages("non-existent-product-id", request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                List<String> tooManyUrls = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    tooManyUrls.add(generateProductImageUrl());
                }

                var request = new AddProductImagesRequest(tooManyUrls);

                ResponseEntity<Void> response = fixture.product().addImages(productId, request);

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
                var request = generateAddProductImagesRequest();

                ResponseEntity<Void> response = anotherFixture.product().addImages(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("DELETE /products/{productId}/images/{imageId}")
    class 상품_이미지_삭제 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getThumbnailImageId(loadProductPort, productId);

                ResponseEntity<Void> response = fixture.product().removeImage(productId, imageId);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getThumbnailImageId(loadProductPort, productId);

                ResponseEntity<Void> response = fixture.product()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.delete("/products/" + productId + "/images/" + imageId)
                                                                            .build(),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = fixture.product().removeImage("non-existent-product-id", "non-existent-image-id");

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }

            @Test
            void 존재하지_않는_이미지이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                ResponseEntity<Void> response = fixture.product().removeImage(productId, "non-existent-image-id");

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getThumbnailImageId(loadProductPort, productId);
                fixture.product().discontinue(productId);

                ResponseEntity<Void> response = fixture.product().removeImage(productId, imageId);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired FixtureSessionFactory fixtureSessionFactory,
                    @Autowired LoadProductPort loadProductPort
            ) {
                FixtureSession anotherFixture = fixtureSessionFactory.create();

                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getThumbnailImageId(loadProductPort, productId);

                anotherFixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = anotherFixture.product().removeImage(productId, imageId);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /products/{productId}/images/{imageId}/thumbnail")
    class 대표이미지_변경 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getFirstNormalImageId(loadProductPort, productId);

                ResponseEntity<Void> response = fixture.product().changeThumbnailImage(productId, imageId);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getFirstNormalImageId(loadProductPort, productId);

                ResponseEntity<Void> response = fixture.product().unauthenticatedClient().exchange(
                        RequestEntity.patch("/products/" + productId + "/images/" + imageId + "/thumbnail").build(),
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = fixture.product()
                                                       .changeThumbnailImage(
                                                               "non-existent-product-id",
                                                               "non-existent-image-id"
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }

            @Test
            void 존재하지_않는_이미지이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                ResponseEntity<Void> response = fixture.product().changeThumbnailImage(productId, "non-existent-image-id");

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadProductPort loadProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getFirstNormalImageId(loadProductPort, productId);
                fixture.product().discontinue(productId);

                ResponseEntity<Void> response = fixture.product().changeThumbnailImage(productId, imageId);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired FixtureSessionFactory fixtureSessionFactory,
                    @Autowired LoadProductPort loadProductPort
            ) {
                FixtureSession anotherFixture = fixtureSessionFactory.create();

                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.product().addImages(productId, generateAddProductImagesRequest());
                String imageId = getFirstNormalImageId(loadProductPort, productId);

                anotherFixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = anotherFixture.product().changeThumbnailImage(productId, imageId);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    private String getThumbnailImageId(LoadProductPort loadProductPort, String productId) {
        Product product = loadProductPort.getByIdWithImages(new ProductId(productId));
        return product.getThumbnailImage().id().value();
    }

    private String getFirstNormalImageId(LoadProductPort loadProductPort, String productId) {
        Product product = loadProductPort.getByIdWithImages(new ProductId(productId));
        return product.getImages().getFirst().id().value();
    }
}
