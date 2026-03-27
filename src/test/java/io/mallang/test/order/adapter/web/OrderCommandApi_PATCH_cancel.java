package io.mallang.test.order.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.OrderFixture.generateCreateOrderRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("PATCH /my/orders/{orderId}/cancel")
class OrderCommandApi_PATCH_cancel {

    @Test
    void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        Product product = generateProduct(5);
        saveProductPort.save(product);

        CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);
        String orderId = fixture.createOrderThenGetId(request);

        // when
        ResponseEntity<Void> response = fixture.cancelOrder(orderId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();

        Product product = generateProduct(5);
        saveProductPort.save(product);

        String orderId = fixture.createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                RequestEntity
                        .patch("/my/orders/" + orderId + "/cancel")
                        .build(),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @Test
    void 존재하지_않는_주문이면_404_Not_Found_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();

        // when
        ResponseEntity<Void> response = fixture.cancelOrder("unknown-order-id");

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void 본인_주문이_아니면_403_Forbidden_상태코드를_반환한다(
            @Autowired TestFixture ordererFixture,
            @Autowired TestFixture anotherFixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        ordererFixture.createMemberThenLogin();

        Product product = generateProduct(5);
        saveProductPort.save(product);

        String orderId = ordererFixture.createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

        anotherFixture.createMemberThenLogin();

        // when
        ResponseEntity<Void> response = anotherFixture.cancelOrder(orderId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void 취소할_수_없는_상태의_주문이면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadOrderPort loadOrderPort,
            @Autowired SaveOrderPort saveOrderPort
    ) {
        // given
        fixture.createMemberThenLogin();

        Product product = generateProduct(5);
        saveProductPort.save(product);

        String orderId = fixture.createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

        Order order = loadOrderPort.getById(new OrderId(orderId));
        order.nextStatus();
        order.nextStatus();

        saveOrderPort.save(order);

        // when
        ResponseEntity<Void> response = fixture.cancelOrder(orderId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}
