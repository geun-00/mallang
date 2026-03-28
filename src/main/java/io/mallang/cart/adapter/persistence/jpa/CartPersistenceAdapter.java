package io.mallang.cart.adapter.persistence.jpa;

import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.exception.CartNotFoundException;
import io.mallang.member.domain.MemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartPersistenceAdapter implements SaveCartPort, LoadCartPort {

    private final CartJpaRepository cartJpaRepository;

    @Override
    public void save(Cart cart) {
        cartJpaRepository.save(CartJpaEntity.from(cart));
    }

    @Override
    public Cart getByMemberId(MemberId memberId) {
        return cartJpaRepository.findWithItemsByMemberId(memberId.value())
                                .map(CartJpaEntity::toDomain)
                                .orElseThrow(() -> new CartNotFoundException(memberId));
    }
}
