package io.mallang.test.order.adapter.web;

import io.mallang.annotations.WebAdapterTest;
import io.mallang.fixtures.api.FixtureSession;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.order.adapter.web.model.OrderDetailResponse;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import io.mallang.stock.application.required.command.SaveStockPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.OrderFixture.generateCreateOrderRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.StockFixture.generateStock;
import static io.mallang.fixtures.api.ApiFixture.ORDERS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@WebAdapterTest
@DisplayName("OrderQuery API")
class OrderQueryApiTest {

    @Nested
    @DisplayName("GET /my/orders")
    class 내_주문_목록_조회 {

        @Nested
        class 성공 {

            @Test
            void 내_주문_목록을_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired SaveStockPort saveStockPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct();
                saveProductPort.save(product);
                saveStockPort.save(generateStock(product, 5));

                String orderId = fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

                ResponseEntity<SearchMyOrdersResponse> response = fixture.order()
                                                                         .searchMyOrders(null, null, 20);

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().items()).hasSize(1);
                assertThat(response.getBody().items().getFirst()).satisfies(item -> {
                    assertThat(item.orderId()).isEqualTo(orderId);
                    assertThat(item.mainProductId()).isEqualTo(product.getId().value());
                    assertThat(item.mainProductName()).isEqualTo(product.getName().value());
                });
            }

            @Test
            void status로_내_주문_목록을_필터링할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired SaveStockPort saveStockPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct();
                saveProductPort.save(product);
                saveStockPort.save(generateStock(product, 5));

                fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 1));

                String canceledOrderId = fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 1));
                fixture.order().cancelOrder(canceledOrderId);

                ResponseEntity<SearchMyOrdersResponse> response = fixture.order()
                                                                         .searchMyOrders("CANCELED", null, 20);

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().items()).extracting("orderId")
                                                      .containsExactly(canceledOrderId);
            }

            @Test
            void size를_지정하면_hasNext와_nextCursor를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired SaveStockPort saveStockPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct();
                saveProductPort.save(product);
                saveStockPort.save(generateStock(product, 5));

                fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 1));
                fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 1));

                ResponseEntity<SearchMyOrdersResponse> response = fixture.order()
                                                                         .searchMyOrders(null, null, 1);

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().items()).hasSize(1);
                assertThat(response.getBody().hasNext()).isTrue();
                assertThat(response.getBody().nextCursor()).isNotBlank();
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                ResponseEntity<String> response = fixture.order()
                                                         .unauthenticatedClient()
                                                         .getForEntity(ORDERS_API, String.class);

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }
    }

    @Nested
    @DisplayName("GET /my/orders/{orderId}")
    class 내_주문_상세_조회 {

        @Nested
        class 성공 {

            @Test
            void 내_주문_상세를_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired SaveStockPort saveStockPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct();
                saveProductPort.save(product);
                saveStockPort.save(generateStock(product, 5));

                String orderId = fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

                ResponseEntity<OrderDetailResponse> response = fixture.order().getOrderDetail(orderId);

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().orderId()).isEqualTo(orderId);
                assertThat(response.getBody().items()).hasSize(1);
                assertThat(response.getBody().items().getFirst().productId()).isEqualTo(product.getId().value());
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                ResponseEntity<String> response = fixture.order()
                                                         .unauthenticatedClient()
                                                         .getForEntity(ORDERS_API + "/order-id", String.class);

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 예외 {

            @Test
            void 존재하지_않는_주문이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<String> response = fixture.order()
                                                         .client()
                                                         .getForEntity(ORDERS_API + "/unknown-order-id", String.class);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }

            @Test
            void 내_주문이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired SaveStockPort saveStockPort
            ) {
                MemberCreateRequest orderer = fixture.auth().createMemberThenLogin();

                Product product = generateProduct();
                saveProductPort.save(product);
                saveStockPort.save(generateStock(product, 5));

                String orderId = fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 1));

                MemberCreateRequest requester = fixture.auth().createMemberThenLogin();

                ResponseEntity<String> response = fixture.order()
                                                         .client()
                                                         .getForEntity(ORDERS_API + "/" + orderId, String.class);

                assertThat(orderer.email()).isNotEqualTo(requester.email());
                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }
}
