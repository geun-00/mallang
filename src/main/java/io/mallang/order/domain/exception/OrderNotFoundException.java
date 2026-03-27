package io.mallang.order.domain.exception;

import io.mallang.domain.common.exception.DomainNotFoundException;
import io.mallang.order.domain.OrderId;

public class OrderNotFoundException extends DomainNotFoundException {

    public OrderNotFoundException(OrderId orderId) {
        super("존재하지 않는 주문입니다. orderId=" + orderId.value());
    }
}
