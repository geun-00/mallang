package io.mallang.fixtures.api;

import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.adapter.web.model.OrderDetailResponse;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public final class OrderApiFixture extends ApiFixture {

    public OrderApiFixture(FixtureContext context) {
        super(context);
    }

    public ResponseEntity<Void> createOrder(CreateOrderRequest request) {
        return client().postForEntity(ORDERS_API, request, Void.class);
    }

    public String createOrderThenGetId(CreateOrderRequest request) {
        ResponseEntity<Void> response = createOrder(request);

        return extractId(response);
    }

    public ResponseEntity<Void> cancelOrder(String orderId) {
        return client().exchange(
                RequestEntity.patch(ORDERS_API + "/" + orderId + "/cancel").build(),
                Void.class
        );
    }

    public ResponseEntity<SearchMyOrdersResponse> searchMyOrders(String status, String lastOrderId, Integer size) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(ORDERS_API);
        addIfPresent(builder, "status", status);
        addIfPresent(builder, "lastOrderId", lastOrderId);
        addIfPresent(builder, "size", size);

        return client().getForEntity(builder.toUriString(), SearchMyOrdersResponse.class);
    }

    public ResponseEntity<OrderDetailResponse> getOrderDetail(String orderId) {
        return client().getForEntity(ORDERS_API + "/" + orderId, OrderDetailResponse.class);
    }

    private void addIfPresent(UriComponentsBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value);
        }
    }
}
