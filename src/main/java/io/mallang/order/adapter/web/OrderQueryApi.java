package io.mallang.order.adapter.web;

import io.mallang.common.adapter.web.auth.CurrentMemberId;
import io.mallang.common.application.query.SliceResult;
import io.mallang.order.adapter.web.model.OrderDetailResponse;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import io.mallang.order.application.provided.query.GetOrderDetailUseCase;
import io.mallang.order.application.provided.query.SearchMyOrdersUseCase;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.mallang.order.adapter.web.mapper.OrderResponseMapper.toOrderDetailResponse;
import static io.mallang.order.adapter.web.mapper.OrderResponseMapper.toSearchMyOrdersResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my/orders")
public class OrderQueryApi {

    private final SearchMyOrdersUseCase searchMyOrdersUseCase;
    private final GetOrderDetailUseCase getOrderDetailUseCase;

    @GetMapping
    public ResponseEntity<SearchMyOrdersResponse> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String lastOrderId,
            @RequestParam(defaultValue = "20") int size,
            @CurrentMemberId String memberId
    ) {
        SliceResult<OrderListView> result = searchMyOrdersUseCase.search(new SearchMyOrdersQuery(
                memberId,
                status,
                lastOrderId,
                size
        ));

        return ResponseEntity.ok(toSearchMyOrdersResponse(result));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> get(
            @PathVariable String orderId,
            @CurrentMemberId String memberId
    ) {
        OrderDetailView result = getOrderDetailUseCase.get(orderId, memberId);

        return ResponseEntity.ok(toOrderDetailResponse(result));
    }
}
