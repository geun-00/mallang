package io.mallang.order.adapter.persistence.mybatis;

import io.mallang.common.application.query.SliceResult;
import io.mallang.order.adapter.persistence.mybatis.model.OrderListRow;
import io.mallang.order.adapter.persistence.mybatis.model.SearchMyOrdersCondition;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;
import io.mallang.order.application.required.query.SearchMyOrdersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryPersistenceAdapter implements SearchMyOrdersPort {

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
}
