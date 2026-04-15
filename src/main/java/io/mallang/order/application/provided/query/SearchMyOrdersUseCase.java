package io.mallang.order.application.provided.query;

import io.mallang.common.application.query.SliceResult;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;

public interface SearchMyOrdersUseCase {

    SliceResult<OrderListView> search(SearchMyOrdersQuery query);
}
