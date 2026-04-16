package io.mallang.order.application.provided.query;

import io.mallang.order.application.provided.query.model.OrderDetailView;

public interface GetOrderDetailUseCase {

    OrderDetailView get(String orderIdValue, String memberIdValue);
}
