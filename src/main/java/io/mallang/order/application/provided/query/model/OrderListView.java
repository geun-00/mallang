package io.mallang.order.application.provided.query.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderListView(
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
