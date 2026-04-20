package io.mallang.order.domain;

import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.order.domain.command.RestoreOrderCommand;
import io.mallang.order.domain.exception.NotOrdererException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Order {

    private final OrderId id;

    private final MemberId memberId;

    private final OrderItems items;

    private final Money totalPrice;

    private final ShippingInfo shippingInfo;

    private final LocalDateTime orderedAt;

    private OrderStatus status;

    private Order(
            OrderId orderId,
            MemberId memberId,
            OrderItems items,
            ShippingInfo shippingInfo,
            LocalDateTime orderedAt
    ) {
        this.id = orderId;
        this.memberId = memberId;
        this.items = items;
        this.totalPrice = items.totalPrice();
        this.shippingInfo = shippingInfo;
        this.status = OrderStatus.PAYMENT_WAITING;
        this.orderedAt = orderedAt;
    }

    public static Order place(
            PlaceOrderCommand command,
            IdGenerator idGenerator,
            ClockHolder clockHolder
    ) {
        return new Order(
                new OrderId(idGenerator.nextId()),
                command.memberId(),
                OrderItems.from(command.items(), idGenerator),
                new ShippingInfo(command.receiver(), command.address()),
                clockHolder.now()
        );
    }

    public static Order restore(RestoreOrderCommand command) {
        Order order = new Order(
                command.id(),
                command.memberId(),
                OrderItems.restore(command.items()),
                command.shippingInfo(),
                command.orderedAt()
        );
        order.status = command.status();
        return order;
    }

    public void nextStatus() {
        this.status = status.next();
    }

    public List<OrderItem> getItems() {
        return items.toList();
    }

    public void cancelBy(MemberId requesterId) {
        validateOrderer(requesterId);
        validateCancelable();

        this.status = OrderStatus.CANCELED;
    }

    private void validateOrderer(MemberId requesterId) {
        if (!memberId.equals(requesterId)) {
            throw new NotOrdererException(id, requesterId, memberId);
        }
    }

    private void validateCancelable() {
        if (!status.isCancelable()) {
            throw new InvalidValueException("취소할 수 없는 주문입니다.");
        }
    }
}
