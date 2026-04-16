package io.mallang.order.adapter.persistence.mybatis.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDetailRow(
        String orderId,
        String memberId,
        String status,
        LocalDateTime orderedAt,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress,
        String orderItemId,
        String productId,
        String productName,
        String productThumbnailImageUrl,
        BigDecimal price,
        int quantity,
        BigDecimal itemTotalPrice
) {
}
