package io.mallang.order.application.service.query;

import io.mallang.common.application.query.SliceResult;
import io.mallang.order.application.provided.query.SearchMyOrdersUseCase;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;
import io.mallang.order.application.required.query.SearchMyOrdersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService implements SearchMyOrdersUseCase {

    private final SearchMyOrdersPort searchMyOrdersPort;

    @Override
    public SliceResult<OrderListView> search(SearchMyOrdersQuery query) {
        return searchMyOrdersPort.search(query);
    }
}
