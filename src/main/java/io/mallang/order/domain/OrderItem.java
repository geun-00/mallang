package io.mallang.order.domain;

import io.mallang.domain.common.Money;
import io.mallang.product.domain.ProductId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem {

    private OrderItemId id;
    private ProductId productId;
    private int quantity;
    private Money price;

    static OrderItem create(OrderItemId id, ProductId productId, int quantity, Money price) {
        if (quantity <= 0)
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        if (price.value().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("주문 상품 가격은 0원보다 커야 합니다.");

        OrderItem item = new OrderItem();

        item.id = id;
        item.productId = productId;
        item.quantity = quantity;
        item.price = price;

        return item;
    }

    Money totalPrice() {
        return price.multiply(quantity);
    }
}
