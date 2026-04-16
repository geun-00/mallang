package io.mallang.order.application.provided.query.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailView(
        String orderId,
        String memberId,
        String status,
        LocalDateTime orderedAt,
        BigDecimal totalPrice,
        String receiverName,
        String receiverPhoneNumber,
        String zipCode,
        String mainAddress,
        String detailAddress,
        List<OrderItemView> items
) {

    public record OrderItemView(
            String orderItemId,
            String productId,
            String productName,
            String productThumbnailImageUrl,
            BigDecimal price,
            int quantity,
            BigDecimal totalPrice
    ) {
    }
}
