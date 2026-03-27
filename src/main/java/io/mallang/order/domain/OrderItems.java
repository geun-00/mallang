package io.mallang.order.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.domain.common.vo.Money;

import java.util.List;

public class OrderItems {

    private final List<OrderItem> items;

    private OrderItems(List<OrderItem> items) {
        validate(items);
        this.items = List.copyOf(items);
    }

    static OrderItems from(List<PlaceOrderItemCommand> commands, IdGenerator idGenerator) {
        List<OrderItem> items = commands.stream()
                                        .map(command -> OrderItem.create(command, idGenerator))
                                        .toList();
        return new OrderItems(items);
    }

    static OrderItems restore(List<OrderItem> items) {
        return new OrderItems(items);
    }

    private static void validate(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidValueException("주문 상품은 1개 이상이어야 합니다.");
        }
    }

    Money totalPrice() {
        return items.stream()
                    .map(OrderItem::totalPrice)
                    .reduce(Money.ZERO, Money::add);
    }

    List<OrderItem> toList() {
        return items;
    }
}
