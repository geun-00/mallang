package io.mallang.test.order.adapter.web;

import io.mallang.annotations.WebAdapterTest;
import io.mallang.fixtures.api.FixtureSession;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.OrderFixture.generateCreateOrderRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.api.ApiFixture.ORDERS_API;
import static org.assertj.core.api.Assertions.assertThat;
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
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

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
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

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
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

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
}
