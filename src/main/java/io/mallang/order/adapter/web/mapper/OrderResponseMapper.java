package io.mallang.order.adapter.web.mapper;

import io.mallang.common.application.query.SliceResult;
import io.mallang.order.adapter.web.model.OrderDetailResponse;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.provided.query.model.OrderListView;

import static io.mallang.order.adapter.web.model.OrderDetailResponse.OrderItemResponse;
import static io.mallang.order.adapter.web.model.SearchMyOrdersResponse.OrderSummary;
import static io.mallang.order.application.provided.query.model.OrderDetailView.OrderItemView;

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

    public static OrderDetailResponse toOrderDetailResponse(OrderDetailView view) {
        return new OrderDetailResponse(
                view.orderId(),
                view.memberId(),
                view.status(),
                view.orderedAt(),
                view.totalPrice(),
                view.receiverName(),
                view.receiverPhoneNumber(),
                view.zipCode(),
                view.mainAddress(),
                view.detailAddress(),
                view.items()
                    .stream()
                    .map(OrderResponseMapper::toOrderItemResponse)
                    .toList()
        );
    }

    private static OrderItemResponse toOrderItemResponse(OrderItemView item) {
        return new OrderItemResponse(
                item.orderItemId(),
                item.productId(),
                item.productName(),
                item.productThumbnailImageUrl(),
                item.price(),
                item.quantity(),
                item.totalPrice()
        );
    }
}
