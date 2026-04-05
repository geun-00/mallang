package io.mallang.test.cart.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItemId;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.CartFixture.generateIdGenerator;
import static io.mallang.fixtures.CartFixture.generateNotExistCartItemId;
import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("PATCH /my/cart/items/{cartItemId}")
class CartCommandApi_PATCH_items {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        Product product = generateProduct(10);
        saveProductPort.save(product);

        String cartItemId = fixture.addCartItemThenGetId(product.getId().value(), 2);

        var request = new ChangeCartItemQuantityRequest(7);

        // when
        ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(
            @Autowired TestFixture fixture
    ) {
        // given
        var request = new ChangeCartItemQuantityRequest(3);

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                RequestEntity
                        .patch("/my/cart/items/cart-item-1")
                        .body(request),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @Test
    void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String cartItemId = "CartItem-1";
        var request = new ChangeCartItemQuantityRequest(null);

        // when
        ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
            int invalidQuantity,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String cartItemId = "CartItem-1";
        var request = new ChangeCartItemQuantityRequest(invalidQuantity);

        // when
        ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_장바구니_항목이면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new ChangeCartItemQuantityRequest(3);

        // when
        ResponseEntity<Void> response = fixture.changeCartItemQuantity(generateNotExistCartItemId().value(), request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired LoadMemberPort loadMemberPort,
            @Autowired LoadCartPort loadCartPort,
            @Autowired SaveCartPort saveCartPort
    ) {
        // given
        MemberCreateRequest memberRequest = generateCreateRequest();
        fixture.registerMember(memberRequest);
        fixture.login(memberRequest.email(), memberRequest.password());
        Member member = loadMemberPort.getByEmail(new Email(memberRequest.email()));

        Cart cart = loadCartPort.getByMemberId(member.getId());
        CartItemId cartItemId = cart.addItem(new AddCartItemCommand("unknown-product-id", 2), generateIdGenerator());
        saveCartPort.save(cart);

        var request = new ChangeCartItemQuantityRequest(3);

        // when
        ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId.value(), request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        Product product = generateProduct(5);
        saveProductPort.save(product);

        String cartItemId = fixture.addCartItemThenGetId(product.getId().value(), 2);

        var request = new ChangeCartItemQuantityRequest(6);

        // when
        ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

}
