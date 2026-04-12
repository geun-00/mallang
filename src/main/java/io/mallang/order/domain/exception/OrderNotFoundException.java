package io.mallang.order.domain.exception;

import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.order.domain.OrderId;

public class OrderNotFoundException extends DomainNotFoundException {

    public OrderNotFoundException(OrderId orderId) {
        super("주문을 찾을 수 없습니다.", "Order를 찾을 수 없습니다 => orderId: " + orderId.value());
    }
}
