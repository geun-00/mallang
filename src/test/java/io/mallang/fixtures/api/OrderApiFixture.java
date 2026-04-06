package io.mallang.fixtures.api;

import io.mallang.order.adapter.web.model.CreateOrderRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

public final class OrderApiFixture extends ApiFixture {

    public OrderApiFixture(FixtureContext context) {
        super(context);
    }

    public ResponseEntity<Void> createOrder(CreateOrderRequest request) {
        return client().postForEntity("/my/orders", request, Void.class);
    }

    public String createOrderThenGetId(CreateOrderRequest request) {
        ResponseEntity<Void> response = createOrder(request);

        return extractId(response);
    }

    public ResponseEntity<Void> cancelOrder(String orderId) {
        return client().exchange(
                RequestEntity.patch("/my/orders/" + orderId + "/cancel").build(),
                Void.class
        );
    }
}
