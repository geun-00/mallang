package io.mallang.test.cart.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("POST /my/cart/items")
class CartCommandApi_POST_items {

    @Test
    void 올바르게_요청하면_201_Created_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        Product product = generateProduct(5);
        saveProductPort.save(product);

        var request = new AddCartItemRequest(product.getId().value(), 2);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        Product product = generateProduct(5);
        saveProductPort.save(product);

        var request = new AddCartItemRequest(product.getId().value(), 2);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

        // then
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.getPath()).startsWith("/my/cart/items/");
        assertThat(location.getPath().replace("/my/cart/items/", "")).isNotBlank();
    }

    @Test
    void 올바르게_요청하면_Location_헤더의_식별자로_장바구니_항목을_확인할_수_있다(
            @Autowired TestFixture fixture,
            @Autowired LoadMemberPort loadMemberPort,
            @Autowired LoadCartPort loadCartPort,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        MemberCreateRequest memberRequest = generateCreateRequest();
        fixture.registerMember(memberRequest);
        fixture.login(memberRequest.email(), memberRequest.password());
        Member member = loadMemberPort.getByEmail(new Email(memberRequest.email()));

        Product product = generateProduct(5);
        saveProductPort.save(product);

        var request = new AddCartItemRequest(product.getId().value(), 2);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

        // then
        String cartItemIdValue = response.getHeaders().getLocation().getPath().substring("/my/cart/items/".length());
        Cart loaded = loadCartPort.getByMemberId(member.getId());

        assertThat(loaded.getItems())
                .extracting(item -> item.getId().value())
                .contains(cartItemIdValue);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(
            @Autowired TestFixture fixture
    ) {
        // given
        var request = new AddCartItemRequest("product-1", 2);

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().postForEntity(
                "/my/cart/items",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void productId_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidProductId,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new AddCartItemRequest(invalidProductId, 2);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new AddCartItemRequest("product-1", null);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

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
        var request = new AddCartItemRequest("product-1", invalidQuantity);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new AddCartItemRequest("unknown-product-id", 2);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

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

        var request = new AddCartItemRequest(product.getId().value(), 6);

        // when
        ResponseEntity<Void> response = fixture.addCartItem(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}
