package io.mallang.test.product.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("PATCH /products/{productId}/discontinue")
class ProductCommandApi_PATCH_discontinue {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();

        // when
        ResponseEntity<Void> response = fixture.discontinue(productId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                RequestEntity
                        .patch("/products/" + productId + "/discontinue")
                        .build(),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @Test
    void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String nonExistentProductId = "non-existent-product-id";

        // when
        ResponseEntity<Void> response = fixture.discontinue(nonExistentProductId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String productId = fixture.registerProductThenGetId();

        fixture.discontinue(productId);

        // when
        ResponseEntity<Void> response = fixture.discontinue(productId);

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

        // when
        ResponseEntity<Void> response = anotherFixture.discontinue(productId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }
}
