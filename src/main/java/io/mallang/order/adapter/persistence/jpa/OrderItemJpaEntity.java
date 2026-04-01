package io.mallang.order.adapter.persistence.jpa;

import io.mallang.adapter.persistence.jpa.BaseEntity;
import io.mallang.domain.common.vo.Money;
import io.mallang.order.domain.OrderItem;
import io.mallang.order.domain.OrderItemId;
import io.mallang.order.domain.command.RestoreOrderItemCommand;
import io.mallang.product.domain.ProductId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class OrderItemJpaEntity extends BaseEntity {

    @Id
    @Column(name = "order_item_id")
    private String id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderJpaEntity order;

    private OrderItemJpaEntity(
            String id,
            String productId,
            int quantity,
            BigDecimal price,
            OrderJpaEntity order
    ) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
    }

    static OrderItemJpaEntity from(OrderItem item, OrderJpaEntity order) {
        return new OrderItemJpaEntity(
                item.getId().value(),
                item.getProductId().value(),
                item.getQuantity(),
                item.getPrice().value(),
                order
        );
    }

    String getId() {
        return id;
    }

    OrderItem toDomain() {
        return OrderItem.restore(new RestoreOrderItemCommand(
                new OrderItemId(id),
                new ProductId(productId),
                quantity,
                new Money(price)
        ));
    }

    void updateFrom(OrderItem item) {
        this.productId = item.getProductId().value();
        this.quantity = item.getQuantity();
        this.price = item.getPrice().value();
    }
}
