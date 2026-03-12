package io.mallang.order.domain;

import io.mallang.domain.common.Address;
import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.Money;
import io.mallang.domain.common.Receiver;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.ProductId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {

    private OrderId id;

    private MemberId memberId;

    private List<OrderItem> items;

    private Money totalPrice;

    private ShippingInfo shippingInfo;

    private OrderStatus status;

    private LocalDateTime orderedAt;

    public static Order place(PlaceOrderCommand command, IdGenerator idGenerator) {
        validateOrderItems(command.items());

        Order order = new Order();

        order.id = new OrderId(idGenerator.nextId());
        order.memberId = new MemberId(command.memberId());
        order.items = createOrderItems(command, idGenerator);
        order.totalPrice = order.calculateTotalPrice();
        order.shippingInfo = createShippingInfo(command);
        order.status = OrderStatus.PAYMENT_WAITING;
        order.orderedAt = LocalDateTime.now();

        return order;
    }

    private static void validateOrderItems(List<OrderItemCommand> items) {
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("주문 상품은 1개 이상이어야 합니다.");
    }

    private static ShippingInfo createShippingInfo(PlaceOrderCommand command) {
        return new ShippingInfo(
                new Receiver(command.receiverName(), command.receiverPhoneNumber()),
                new Address(command.zipCode(), command.mainAddress(), command.detailAddress())
        );
    }

    private static List<OrderItem> createOrderItems(PlaceOrderCommand command, IdGenerator idGenerator) {
        return command.items()
                      .stream()
                      .map(itemCommand -> OrderItem.create(
                              new OrderItemId(idGenerator.nextId()),
                              new ProductId(itemCommand.productId()),
                              itemCommand.quantity(),
                              new Money(BigDecimal.valueOf(itemCommand.price()))
                      ))
                      .toList();
    }

    public void cancel() {
        if (!status.isCancelable())
            throw new IllegalStateException("취소할 수 없는 주문입니다.");

        this.status = OrderStatus.CANCELED;
    }

    public void nextStatus() {
        this.status = status.next();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    private Money calculateTotalPrice() {
        return items.stream()
                    .map(OrderItem::totalPrice)
                    .reduce(new Money(BigDecimal.ZERO), Money::add);
    }
}
