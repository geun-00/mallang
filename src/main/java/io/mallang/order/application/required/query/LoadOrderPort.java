package io.mallang.order.application.required.query;

import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.exception.OrderNotFoundException;

public interface LoadOrderPort {

    /**
     * @throws OrderNotFoundException
     */
    Order getById(OrderId orderId);
}
