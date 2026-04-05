package io.mallang.test.cart.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.product.application.required.command.SaveProductPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.CartFixture.generateNotExistCartItemId;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("DELETE /my/cart/items/{cartItemId}")
class CartCommandApi_DELETE_items {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        var product = generateProduct(5);
        saveProductPort.save(product);

        String cartItemId = fixture.addCartItemThenGetId(product.getId().value(), 2);

        // when
        ResponseEntity<Void> response = fixture.removeCartItem(cartItemId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(
            @Autowired TestFixture fixture
    ) {
        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient()
                                               .exchange(
                                                       RequestEntity
                                                               .delete("/my/cart/items/cart-item-1")
                                                               .build(),
                                                       Void.class
                                               );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @Test
    void 존재하지_않는_장바구니_항목이면_404_Not_Found_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();

        // when
        ResponseEntity<Void> response = fixture.removeCartItem(generateNotExistCartItemId().value());

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}
