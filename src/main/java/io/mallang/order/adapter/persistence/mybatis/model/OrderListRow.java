package io.mallang.order.adapter.persistence.mybatis.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderListRow(
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
