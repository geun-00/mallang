package io.mallang.order.adapter.web.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
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
        List<OrderItemResponse> items
) {

    public record OrderItemResponse(
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
