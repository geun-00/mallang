package io.mallang.order.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.Money;
import io.mallang.product.domain.ProductId;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItem {

    private final OrderItemId id;

    private final ProductId productId;

    private final int quantity;

    private final Money price;

    private OrderItem(OrderItemId id, ProductId productId, int quantity, Money price) {
        validateQuantity(quantity);
        validatePrice(price);

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    static OrderItem create(OrderItemCommand itemCommand, IdGenerator idGenerator) {
        return new OrderItem(
                new OrderItemId(idGenerator.nextId()),
                new ProductId(itemCommand.productId()),
                itemCommand.quantity(),
                new Money(itemCommand.price())
        );
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
    }

    private static void validatePrice(Money price) {
        if (price.value().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("주문 상품 가격은 0원보다 커야 합니다.");
    }

    Money totalPrice() {
        return price.multiply(quantity);
    }
}
