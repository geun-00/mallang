package io.mallang.test.cart.adapter.web;

import io.mallang.annotations.WebAdapterTest;
import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.fixtures.api.FixtureSession;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.application.required.command.SaveStockPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static io.mallang.fixtures.CommonFixture.generateIdGenerator;
import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.StockFixture.generateStock;
import static io.mallang.fixtures.api.ApiFixture.CART_ITEMS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

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
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                var request = new AddCartItemRequest(productId, 2);

                ResponseEntity<Void> response = fixture.cart().addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                var request = new AddCartItemRequest(productId, 2);

                ResponseEntity<Void> response = fixture.cart().addCartItem(request);

                URI location = response.getHeaders().getLocation();
                assertThat(location).isNotNull();
                assertThat(location.getPath()).startsWith(CART_ITEMS_API + "/");
                assertThat(location.getPath().replace(CART_ITEMS_API + "/", "")).isNotBlank();
            }

            @Test
            void 올바르게_요청하면_Location_헤더의_식별자로_장바구니_항목을_확인할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadMemberPort loadMemberPort,
                    @Autowired LoadCartPort loadCartPort
            ) {
                MemberCreateRequest memberCreateRequest = fixture.auth().createMemberThenLogin();
                Member member = loadMemberPort.getByEmail(new Email(memberCreateRequest.email()));

                String productId = fixture.product().registerProductThenGetId();

                var request = new AddCartItemRequest(productId, 2);

                ResponseEntity<Void> response = fixture.cart().addCartItem(request);

                String cartItemIdValue = response.getHeaders()
                                                 .getLocation()
                                                 .getPath()
                                                 .substring((CART_ITEMS_API + "/").length());
                Cart loaded = loadCartPort.getByMemberId(member.getId());

                assertThat(loaded.getItems()).extracting(item -> item.getId().value())
                                             .contains(cartItemIdValue);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                var request = new AddCartItemRequest("product-1", 2);

                ResponseEntity<Void> response = fixture.cart()
                                                       .unauthenticatedClient()
                                                       .postForEntity(CART_ITEMS_API, request, Void.class);

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                var request = new AddCartItemRequest("unknown-product-id", 2);

                ResponseEntity<Void> response = fixture.cart().addCartItem(request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired SaveStockPort saveStockPort
            ) {
                fixture.auth().createMemberThenLogin();
                Product product = generateProduct();
                saveProductPort.save(product);
                saveStockPort.save(generateStock(product, 5));

                var request = new AddCartItemRequest(product.getId().value(), 6);

                ResponseEntity<Void> response = fixture.cart().addCartItem(request);

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
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                String cartItemId = fixture.cart().addCartItemThenGetId(productId, 2);
                var request = new ChangeCartItemQuantityRequest(7);

                ResponseEntity<Void> response = fixture.cart().changeCartItemQuantity(cartItemId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                var request = new ChangeCartItemQuantityRequest(3);

                ResponseEntity<Void> response = fixture.cart()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.patch(CART_ITEMS_API + "/cart-item-1")
                                                                            .body(request),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_장바구니_항목이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                var request = new ChangeCartItemQuantityRequest(3);

                ResponseEntity<Void> response = fixture.cart()
                                                       .changeCartItemQuantity(
                                                               new CartItemId(UUID.randomUUID().toString()).value(),
                                                               request
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadMemberPort loadMemberPort,
                    @Autowired LoadCartPort loadCartPort,
                    @Autowired SaveCartPort saveCartPort
            ) {
                MemberCreateRequest memberRequest = generateCreateRequest();
                fixture.member().registerMember(memberRequest);
                fixture.auth().login(memberRequest.email(), memberRequest.password());
                Member member = loadMemberPort.getByEmail(new Email(memberRequest.email()));

                Cart cart = loadCartPort.getByMemberId(member.getId());
                CartItemId cartItemId = cart.addItem(
                        new AddCartItemCommand(new ProductId("unknown-product-id"), 2),
                        generateIdGenerator()
                );
                saveCartPort.save(cart);

                var request = new ChangeCartItemQuantityRequest(3);

                ResponseEntity<Void> response = fixture.cart().changeCartItemQuantity(cartItemId.value(), request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired SaveStockPort saveStockPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct();
                saveProductPort.save(product);
                saveStockPort.save(generateStock(product, 5));

                String cartItemId = fixture.cart().addCartItemThenGetId(product.getId().value(), 2);
                var request = new ChangeCartItemQuantityRequest(6);

                ResponseEntity<Void> response = fixture.cart().changeCartItemQuantity(cartItemId, request);

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
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                String cartItemId = fixture.cart().addCartItemThenGetId(productId, 2);

                ResponseEntity<Void> response = fixture.cart().removeCartItem(cartItemId);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                ResponseEntity<Void> response = fixture.cart()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.delete(CART_ITEMS_API + "/cart-item-1")
                                                                            .build(),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_장바구니_항목이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = fixture.cart().removeCartItem(new CartItemId(UUID.randomUUID().toString()).value());

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
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.cart().addCartItem(new AddCartItemRequest(productId, 2));

                ResponseEntity<Void> response = fixture.cart().clearCart();

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                ResponseEntity<Void> response = fixture.cart()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.delete(CART_ITEMS_API).build(),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }
    }
}
