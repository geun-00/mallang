package io.mallang.order.domain;

import io.mallang.domain.common.ClockHolder;
import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Money;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.member.domain.MemberId;
import io.mallang.order.domain.command.RestoreOrderCommand;
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

    private Order(OrderId orderId,
                  MemberId memberId,
                  OrderItems items,
                  ShippingInfo shippingInfo,
                  LocalDateTime orderedAt) {

        this.id = orderId;
        this.memberId = memberId;
        this.items = items;
        this.totalPrice = items.totalPrice();
        this.shippingInfo = shippingInfo;
        this.status = OrderStatus.PAYMENT_WAITING;
        this.orderedAt = orderedAt;
    }

    public static Order place(PlaceOrderCommand command, IdGenerator idGenerator, ClockHolder clockHolder) {
        return new Order(
                new OrderId(idGenerator.nextId()),
                new MemberId(command.memberId()),
                OrderItems.from(command.items(), idGenerator),
                createShippingInfo(command),
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

    private static ShippingInfo createShippingInfo(PlaceOrderCommand command) {
        return new ShippingInfo(
                new Receiver(command.receiverName(), command.receiverPhoneNumber()),
                new Address(command.zipCode(), command.mainAddress(), command.detailAddress())
        );
    }

    public void cancel() {
        if (!status.isCancelable()) {
            throw new IllegalStateException("취소할 수 없는 주문입니다.");
        }

        this.status = OrderStatus.CANCELED;
    }

    public void nextStatus() {
        this.status = status.next();
    }

    public List<OrderItem> getItems() {
        return items.toList();
    }
}
