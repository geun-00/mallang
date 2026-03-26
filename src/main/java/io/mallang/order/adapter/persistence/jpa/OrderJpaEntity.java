package io.mallang.order.adapter.persistence.jpa;

import io.mallang.member.domain.MemberId;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.OrderStatus;
import io.mallang.order.domain.ShippingInfo;
import io.mallang.order.domain.command.RestoreOrderCommand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity {

    @Id
    @Column(name = "order_id")
    private String id;

    @Column(nullable = false)
    private String memberId;

    @Embedded
    private ReceiverJpaVO receiver;

    @Embedded
    private AddressJpaVO address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    private OrderJpaEntity(
            String id,
            String memberId,
            ReceiverJpaVO receiver,
            AddressJpaVO address,
            OrderStatus status,
            LocalDateTime orderedAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.receiver = receiver;
        this.address = address;
        this.status = status;
        this.orderedAt = orderedAt;
    }

    public static OrderJpaEntity from(Order order) {
        ShippingInfo shippingInfo = order.getShippingInfo();
        OrderJpaEntity entity = new OrderJpaEntity(
                order.getId().value(),
                order.getMemberId().value(),
                ReceiverJpaVO.from(shippingInfo.receiver()),
                AddressJpaVO.from(shippingInfo.address()),
                order.getStatus(),
                order.getOrderedAt()
        );

        order.getItems().stream()
             .map(item -> OrderItemJpaEntity.from(item, entity))
             .forEach(entity.items::add);

        return entity;
    }

    public Order toDomain() {
        return Order.restore(new RestoreOrderCommand(
                new OrderId(id),
                new MemberId(memberId),
                items.stream().map(OrderItemJpaEntity::toDomain).toList(),
                new ShippingInfo(receiver.toDomain(), address.toDomain()),
                status,
                orderedAt
        ));
    }
}
