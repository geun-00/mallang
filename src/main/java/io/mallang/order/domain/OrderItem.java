package io.mallang.order.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.domain.common.vo.Money;
import io.mallang.order.domain.command.PlaceOrderItemCommand;
import io.mallang.order.domain.command.RestoreOrderItemCommand;
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

    static OrderItem create(PlaceOrderItemCommand itemCommand, IdGenerator idGenerator) {
        return new OrderItem(
                new OrderItemId(idGenerator.nextId()),
                new ProductId(itemCommand.productId()),
                itemCommand.quantity(),
                new Money(itemCommand.price())
        );
    }

    public static OrderItem restore(RestoreOrderItemCommand command) {
        return new OrderItem(
                command.id(),
                command.productId(),
                command.quantity(),
                command.price()
        );
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidValueException("주문 수량은 1개 이상이어야 합니다.");
        }
    }

    private void validatePrice(Money price) {
        if (price.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidValueException("주문 상품 가격은 0원보다 커야 합니다.");
        }
    }

    Money totalPrice() {
        return price.multiply(quantity);
    }
}
