package io.mallang.test.cart.adapter.web;

import io.mallang.TestFixture;
import io.mallang.WebAdapterTest;
import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.product.application.required.command.SaveProductPort;
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

import java.net.URI;

import static io.mallang.fixtures.CartFixture.generateIdGenerator;
import static io.mallang.fixtures.CartFixture.generateNotExistCartItemId;
import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@WebAdapterTest
@DisplayName("CartCommand API")
class CartCommandApiTest {

    @Nested
    @DisplayName("POST /my/cart/items")
    class 항목_추가 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_201_Created_상태코드를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                var request = new AddCartItemRequest(product.getId().value(), 2);

                ResponseEntity<Void> response = fixture.addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                var request = new AddCartItemRequest(product.getId().value(), 2);

                ResponseEntity<Void> response = fixture.addCartItem(request);

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
                MemberCreateRequest memberRequest = generateCreateRequest();
                fixture.registerMember(memberRequest);
                fixture.login(memberRequest.email(), memberRequest.password());
                Member member = loadMemberPort.getByEmail(new Email(memberRequest.email()));

                Product product = generateProduct(5);
                saveProductPort.save(product);

                var request = new AddCartItemRequest(product.getId().value(), 2);

                ResponseEntity<Void> response = fixture.addCartItem(request);

                String cartItemIdValue = response.getHeaders().getLocation().getPath().substring("/my/cart/items/".length());
                Cart loaded = loadCartPort.getByMemberId(member.getId());

                assertThat(loaded.getItems())
                        .extracting(item -> item.getId().value())
                        .contains(cartItemIdValue);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                var request = new AddCartItemRequest("product-1", 2);

                ResponseEntity<Void> response = fixture.unauthenticatedClient().postForEntity(
                        "/my/cart/items",
                        request,
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {"", " "})
            void productId_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidProductId,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new AddCartItemRequest(invalidProductId, 2);

                ResponseEntity<Void> response = fixture.addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new AddCartItemRequest("product-1", null);

                ResponseEntity<Void> response = fixture.addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @ValueSource(ints = {0, -1})
            void quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
                    int invalidQuantity,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new AddCartItemRequest("product-1", invalidQuantity);

                ResponseEntity<Void> response = fixture.addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new AddCartItemRequest("unknown-product-id", 2);

                ResponseEntity<Void> response = fixture.addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                var request = new AddCartItemRequest(product.getId().value(), 6);

                ResponseEntity<Void> response = fixture.addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /my/cart/items/{cartItemId}")
    class 수량_변경 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.createMemberThenLogin();

                Product product = generateProduct(10);
                saveProductPort.save(product);

                String cartItemId = fixture.addCartItemThenGetId(product.getId().value(), 2);
                var request = new ChangeCartItemQuantityRequest(7);

                ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                var request = new ChangeCartItemQuantityRequest(3);

                ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                        RequestEntity.patch("/my/cart/items/cart-item-1").body(request),
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
                var request = new ChangeCartItemQuantityRequest(null);

                ResponseEntity<Void> response = fixture.changeCartItemQuantity("CartItem-1", request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @ValueSource(ints = {0, -1})
            void quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
                    int invalidQuantity,
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new ChangeCartItemQuantityRequest(invalidQuantity);

                ResponseEntity<Void> response = fixture.changeCartItemQuantity("CartItem-1", request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_장바구니_항목이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();
                var request = new ChangeCartItemQuantityRequest(3);

                ResponseEntity<Void> response = fixture.changeCartItemQuantity(generateNotExistCartItemId().value(), request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired LoadMemberPort loadMemberPort,
                    @Autowired LoadCartPort loadCartPort,
                    @Autowired SaveCartPort saveCartPort
            ) {
                MemberCreateRequest memberRequest = generateCreateRequest();
                fixture.registerMember(memberRequest);
                fixture.login(memberRequest.email(), memberRequest.password());
                Member member = loadMemberPort.getByEmail(new Email(memberRequest.email()));

                Cart cart = loadCartPort.getByMemberId(member.getId());
                CartItemId cartItemId = cart.addItem(new AddCartItemCommand(new ProductId("unknown-product-id"), 2), generateIdGenerator());
                saveCartPort.save(cart);

                var request = new ChangeCartItemQuantityRequest(3);

                ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId.value(), request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                String cartItemId = fixture.addCartItemThenGetId(product.getId().value(), 2);
                var request = new ChangeCartItemQuantityRequest(6);

                ResponseEntity<Void> response = fixture.changeCartItemQuantity(cartItemId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }

    @Nested
    @DisplayName("DELETE /my/cart/items/{cartItemId}")
    class 항목_제거 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.createMemberThenLogin();

                var product = generateProduct(5);
                saveProductPort.save(product);

                String cartItemId = fixture.addCartItemThenGetId(product.getId().value(), 2);

                ResponseEntity<Void> response = fixture.removeCartItem(cartItemId);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                        RequestEntity.delete("/my/cart/items/cart-item-1").build(),
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_장바구니_항목이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired TestFixture fixture
            ) {
                fixture.createMemberThenLogin();

                ResponseEntity<Void> response = fixture.removeCartItem(generateNotExistCartItemId().value());

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("DELETE /my/cart/items")
    class 전체_비우기 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
                    @Autowired TestFixture fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.createMemberThenLogin();

                var product = generateProduct(5);
                saveProductPort.save(product);
                fixture.addCartItem(new AddCartItemRequest(product.getId().value(), 2));

                ResponseEntity<Void> response = fixture.clearCart();

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
                ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                        RequestEntity.delete("/my/cart/items").build(),
                        Void.class
                );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }
    }
}
