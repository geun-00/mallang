package io.mallang.cart.adapter.persistence.jpa;

import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.command.RestoreCartCommand;
import io.mallang.member.domain.MemberId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartJpaEntity {

    @Id
    @Column(name = "member_id")
    private String memberId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemJpaEntity> items = new ArrayList<>();

    private CartJpaEntity(String memberId) {
        this.memberId = memberId;
    }

    public static CartJpaEntity from(Cart cart) {
        CartJpaEntity entity = new CartJpaEntity(cart.getMemberId().value());

        cart.getItems().stream()
            .map(item -> CartItemJpaEntity.from(item, entity))
            .forEach(entity.items::add);

        return entity;
    }

    public Cart toDomain() {
        return Cart.restore(new RestoreCartCommand(
                new MemberId(memberId),
                items.stream().map(CartItemJpaEntity::toDomain).toList()
        ));
    }
}
