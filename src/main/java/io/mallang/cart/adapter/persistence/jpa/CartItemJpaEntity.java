package io.mallang.cart.adapter.persistence.jpa;

import io.mallang.cart.domain.CartItem;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.RestoreCartItemCommand;
import io.mallang.product.domain.ProductId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItemJpaEntity {

    @Id
    @Column(name = "cart_item_id")
    private String id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private CartJpaEntity cart;

    private CartItemJpaEntity(String id, String productId, int quantity, CartJpaEntity cart) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.cart = cart;
    }

    public static CartItemJpaEntity from(CartItem item, CartJpaEntity cart) {
        return new CartItemJpaEntity(
                item.getId().value(),
                item.getProductId().value(),
                item.getQuantity(),
                cart
        );
    }

    public CartItem toDomain() {
        return CartItem.restore(new RestoreCartItemCommand(
                new CartItemId(id),
                new ProductId(productId),
                quantity
        ));
    }
}
