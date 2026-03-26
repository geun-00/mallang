package io.mallang.test.product.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static io.mallang.fixtures.ProductFixture.generateAddProductImagesRequest;
import static io.mallang.fixtures.ProductFixture.generateProductImageUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("POST /products/{productId}/images")
class ProductImageCommandApi_POST_images {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = generateAddProductImagesRequest();

        // when
        ResponseEntity<Void> response = fixture.addImages(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = generateAddProductImagesRequest();

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                RequestEntity
                        .post("/products/" + productId + "/images")
                        .body(request),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @Test
    void imageUrls_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();
        var request = new AddProductImagesRequest(null);

        // when
        ResponseEntity<Void> response = fixture.addImages(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String nonExistentProductId = "non-existent-product-id";
        var request = generateAddProductImagesRequest();

        // when
        ResponseEntity<Void> response = fixture.addImages(nonExistentProductId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();

        List<String> tooManyUrls = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            tooManyUrls.add(generateProductImageUrl());
        }

        var request = new AddProductImagesRequest(tooManyUrls);

        // when
        ResponseEntity<Void> response = fixture.addImages(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
            @Autowired TestFixture ownerFixture,
            @Autowired TestFixture anotherFixture
    ) {
        // given
        ownerFixture.createMemberThenLogin();
        String productId = ownerFixture.registerProductThenGetId();

        anotherFixture.createMemberThenLogin();
        var request = generateAddProductImagesRequest();

        // when
        ResponseEntity<Void> response = anotherFixture.addImages(productId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }
}
