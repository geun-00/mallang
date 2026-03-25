package io.mallang.test.product.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.ProductFixture.generateProductDescription;
import static io.mallang.fixtures.ProductFixture.generateProductName;
import static io.mallang.fixtures.ProductFixture.generateProductPriceAmount;
import static io.mallang.fixtures.ProductFixture.generateUpdateProductRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("PUT /products/{productId}")
class ProductCommandApi_PUT {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = generateUpdateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.updateProduct(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = generateUpdateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                RequestEntity
                        .put("/products/" + productId)
                        .body(request),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void name_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidName,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = new UpdateProductRequest(
                invalidName,
                generateProductDescription(),
                generateProductPriceAmount(),
                "BOOKS"
        );

        // when
        ResponseEntity<Void> response = fixture.updateProduct(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void description_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = new UpdateProductRequest(
                generateProductName(),
                null,
                generateProductPriceAmount(),
                "BOOKS"
        );

        // when
        ResponseEntity<Void> response = fixture.updateProduct(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void price_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = new UpdateProductRequest(
                generateProductName(),
                generateProductDescription(),
                null,
                "BOOKS"
        );

        // when
        ResponseEntity<Void> response = fixture.updateProduct(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void category_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidCategory,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = new UpdateProductRequest(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                invalidCategory
        );

        // when
        ResponseEntity<Void> response = fixture.updateProduct(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String nonExistentProductId = "non-existent-product-id";
        var request = generateUpdateProductRequest();

        // when
        ResponseEntity<Void> response = fixture.updateProduct(nonExistentProductId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = new UpdateProductRequest(
                generateProductName(),
                generateProductDescription(),
                generateProductPriceAmount(),
                "invalid-Category"
        );

        // when
        ResponseEntity<Void> response = fixture.updateProduct(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}
