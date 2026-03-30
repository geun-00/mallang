package io.mallang.test.cart.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.product.application.required.command.SaveProductPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("DELETE /my/cart/items")
class CartCommandApi_DELETE_items_all {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        var product = generateProduct(5);
        saveProductPort.save(product);
        fixture.addCartItem(new AddCartItemRequest(product.getId().value(), 2));

        // when
        ResponseEntity<Void> response = fixture.clearCart();

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
                                                               .delete("/my/cart/items")
                                                               .build(),
                                                       Void.class
                                               );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }
}
