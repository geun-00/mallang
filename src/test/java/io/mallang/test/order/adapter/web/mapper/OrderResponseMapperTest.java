package io.mallang.test.order.adapter.web.mapper;

import io.mallang.annotations.MapperTest;
import io.mallang.common.application.query.SliceResult;
import io.mallang.order.adapter.web.mapper.OrderResponseMapper;
import io.mallang.order.adapter.web.model.OrderDetailResponse;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static io.mallang.assertions.OrderAssertions.isMappedFrom;
import static org.assertj.core.api.Assertions.assertThat;

@MapperTest
@DisplayName("OrderResponse Mapper")
class OrderResponseMapperTest {

    @Test
    void 주문_목록_결과를_응답으로_변환할_수_있다() {
        SliceResult<OrderListView> result = new SliceResult<>(
                List.of(new OrderListView(
                        "order-1",
                        OrderStatus.PAYMENT_WAITING.name(),
                        LocalDateTime.of(2024, 1, 1, 0, 0),
                        BigDecimal.valueOf(6000),
                        2,
                        "product-1",
                        "Apple",
                        "https://example.com/apple.jpg"
                )),
                true,
                "order-1"
        );

        SearchMyOrdersResponse response = OrderResponseMapper.toSearchMyOrdersResponse(result);

        assertThat(response).satisfies(isMappedFrom(result));
    }

    @Test
    void 주문_상세_뷰를_응답으로_변환할_수_있다() {
        OrderDetailView view = new OrderDetailView(
                "order-1",
                "member-1",
                OrderStatus.PAYMENT_WAITING.name(),
                LocalDateTime.of(2024, 1, 1, 0, 0),
                BigDecimal.valueOf(6000),
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호",
                List.of(new OrderDetailView.OrderItemView(
                        "order-item-1",
                        "product-1",
                        "Apple",
                        "https://example.com/apple.jpg",
                        BigDecimal.valueOf(3000),
                        2,
                        BigDecimal.valueOf(6000)
                ))
        );

        OrderDetailResponse response = OrderResponseMapper.toOrderDetailResponse(view);

        assertThat(response).satisfies(isMappedFrom(view));
    }
}
