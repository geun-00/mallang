package io.mallang.order.adapter.persistence.jpa;

import io.mallang.adapter.persistence.jpa.BaseEntity;
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
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity extends BaseEntity {

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

    static OrderJpaEntity from(Order order) {
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

    Order toDomain() {
        return Order.restore(new RestoreOrderCommand(
                new OrderId(id),
                new MemberId(memberId),
                items.stream()
                     .map(OrderItemJpaEntity::toDomain)
                     .toList(),
                new ShippingInfo(receiver.toDomain(), address.toDomain()),
                status,
                orderedAt
        ));
    }

    void updateFrom(Order order) {
        ShippingInfo shippingInfo = order.getShippingInfo();

        this.memberId = order.getMemberId().value();
        this.receiver = ReceiverJpaVO.from(shippingInfo.receiver());
        this.address = AddressJpaVO.from(shippingInfo.address());
        this.status = order.getStatus();
        this.orderedAt = order.getOrderedAt();

        syncItems(order);
    }

    private void syncItems(Order order) {
        Map<String, OrderItemJpaEntity> existingItemsById = indexExistingItemsById();
        Set<String> targetItemIds = collectTargetItemIds(order);

        removeItemsFrom(targetItemIds);
        upsertItems(order, existingItemsById);
    }

    private Map<String, OrderItemJpaEntity> indexExistingItemsById() {
        return items.stream()
                    .collect(toMap(OrderItemJpaEntity::getId, identity()));
    }

    private Set<String> collectTargetItemIds(Order order) {
        return order.getItems()
                    .stream()
                    .map(item -> item.getId().value())
                    .collect(toSet());
    }

    private void removeItemsFrom(Set<String> targetItemIds) {
        this.items.removeIf(item -> !targetItemIds.contains(item.getId()));
    }

    private void upsertItems(Order order, Map<String, OrderItemJpaEntity> existingItemsById) {
        order.getItems().forEach(item -> {
            OrderItemJpaEntity existingItem = existingItemsById.get(item.getId().value());

            if (existingItem == null) {
                this.items.add(OrderItemJpaEntity.from(item, this));
                return;
            }

            existingItem.updateFrom(item);
        });
    }
}
