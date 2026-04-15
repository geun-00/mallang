package io.mallang.order.adapter.web.mapper;

import io.mallang.common.application.query.SliceResult;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import io.mallang.order.application.provided.query.model.OrderListView;

import static io.mallang.order.adapter.web.model.SearchMyOrdersResponse.OrderSummary;

public final class OrderResponseMapper {

    private OrderResponseMapper() {
    }

    public static SearchMyOrdersResponse toSearchMyOrdersResponse(SliceResult<OrderListView> result) {
        return new SearchMyOrdersResponse(
                result.items()
                      .stream()
                      .map(OrderResponseMapper::toOrderSummary)
                      .toList(),
                result.hasNext(),
                result.nextCursor()
        );
    }

    private static OrderSummary toOrderSummary(OrderListView item) {
        return new OrderSummary(
                item.orderId(),
                item.status(),
                item.orderedAt(),
                item.totalPrice(),
                item.itemCount(),
                item.mainProductId(),
                item.mainProductName(),
                item.mainProductThumbnailImageUrl()
        );
    }
}
