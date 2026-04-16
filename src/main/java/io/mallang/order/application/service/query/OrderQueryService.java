package io.mallang.order.application.service.query;

import io.mallang.common.application.query.SliceResult;
import io.mallang.member.domain.MemberId;
import io.mallang.order.application.provided.query.GetOrderDetailUseCase;
import io.mallang.order.application.provided.query.SearchMyOrdersUseCase;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;
import io.mallang.order.application.required.query.LoadOrderDetailPort;
import io.mallang.order.application.required.query.SearchMyOrdersPort;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.exception.NotOrdererException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService implements SearchMyOrdersUseCase, GetOrderDetailUseCase {

    private final SearchMyOrdersPort searchMyOrdersPort;
    private final LoadOrderDetailPort loadOrderDetailPort;

    @Override
    public SliceResult<OrderListView> search(SearchMyOrdersQuery query) {
        return searchMyOrdersPort.search(query);
    }

    @Override
    public OrderDetailView get(String orderIdValue, String memberIdValue) {
        OrderDetailView detail = loadOrderDetailPort.load(orderIdValue);

        if (!detail.memberId().equals(memberIdValue)) {
            throw new NotOrdererException(
                    new OrderId(orderIdValue),
                    new MemberId(memberIdValue),
                    new MemberId(detail.memberId())
            );
        }

        return detail;
    }
}
