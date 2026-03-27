package io.mallang.order.application.required.command;

import io.mallang.order.domain.Order;

public interface SaveOrderPort {

    void save(Order order);
}
