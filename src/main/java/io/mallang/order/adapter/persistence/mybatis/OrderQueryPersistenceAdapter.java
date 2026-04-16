package io.mallang.order.adapter.persistence.mybatis;

import io.mallang.common.application.query.SliceResult;
import io.mallang.order.adapter.persistence.mybatis.model.OrderDetailRow;
import io.mallang.order.adapter.persistence.mybatis.model.OrderListRow;
import io.mallang.order.adapter.persistence.mybatis.model.SearchMyOrdersCondition;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;
import io.mallang.order.application.required.query.LoadOrderDetailPort;
import io.mallang.order.application.required.query.SearchMyOrdersPort;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static io.mallang.order.application.provided.query.model.OrderDetailView.OrderItemView;

@Repository
@RequiredArgsConstructor
public class OrderQueryPersistenceAdapter implements SearchMyOrdersPort, LoadOrderDetailPort {

    private final OrderQueryMapper orderQueryMapper;

    @Override
    public SliceResult<OrderListView> search(SearchMyOrdersQuery query) {
        SearchMyOrdersCondition condition = new SearchMyOrdersCondition(
                query.memberId(),
                query.status(),
                query.lastOrderId(),
                query.size() + 1
        );

        List<OrderListView> loadedItems = orderQueryMapper.selectMyOrders(condition)
                                                          .stream()
                                                          .map(this::convertToView)
                                                          .toList();

        return SliceResult.of(loadedItems, query.size(), OrderListView::orderId);
    }

    private OrderListView convertToView(OrderListRow row) {
        return new OrderListView(
                row.orderId(),
                row.status(),
                row.orderedAt(),
                row.totalPrice(),
                row.itemCount(),
                row.mainProductId(),
                row.mainProductName(),
                row.mainProductThumbnailImageUrl()
        );
    }

    @Override
    public OrderDetailView load(String orderIdValue) {
        List<OrderDetailRow> rows = orderQueryMapper.selectOrderDetailRows(orderIdValue);
        if (rows.isEmpty()) {
            throw new OrderNotFoundException(new OrderId(orderIdValue));
        }

        OrderDetailRow common = rows.getFirst();
        List<OrderItemView> items = rows.stream()
                                        .map(this::convertToView)
                                        .toList();
        BigDecimal totalPrice = rows.stream()
                                    .map(OrderDetailRow::itemTotalPrice)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderDetailView(
                common.orderId(),
                common.memberId(),
                common.status(),
                common.orderedAt(),
                totalPrice,
                common.receiverName(),
                common.receiverPhoneNumber(),
                common.zipCode(),
                common.mainAddress(),
                common.detailAddress(),
                items
        );
    }

    private OrderItemView convertToView(OrderDetailRow row) {
        return new OrderItemView(
                row.orderItemId(),
                row.productId(),
                row.productName(),
                row.productThumbnailImageUrl(),
                row.price(),
                row.quantity(),
                row.itemTotalPrice()
        );
    }
}
