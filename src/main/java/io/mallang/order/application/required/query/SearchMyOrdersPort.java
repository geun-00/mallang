package io.mallang.order.application.required.query;

import io.mallang.common.application.query.SliceResult;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;

public interface SearchMyOrdersPort {

    SliceResult<OrderListView> search(SearchMyOrdersQuery query);
}
