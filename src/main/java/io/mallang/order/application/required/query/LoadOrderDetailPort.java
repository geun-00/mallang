package io.mallang.order.application.required.query;

import io.mallang.order.application.provided.query.model.OrderDetailView;

public interface LoadOrderDetailPort {

    OrderDetailView load(String orderIdValue);
}
