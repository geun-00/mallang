package io.mallang.order.adapter.web.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SearchMyOrdersResponse(
        List<OrderSummary> items,
        boolean hasNext,
        String nextCursor
) {
    public record OrderSummary(
            String orderId,
            String status,
            LocalDateTime orderedAt,
            BigDecimal totalPrice,
            int itemCount,
            String mainProductId,
            String mainProductName,
            String mainProductThumbnailImageUrl
    ) {
    }
}
