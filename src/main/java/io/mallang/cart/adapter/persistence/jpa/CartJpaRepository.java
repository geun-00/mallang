package io.mallang.cart.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartJpaRepository extends JpaRepository<CartJpaEntity, String> {

    @EntityGraph(attributePaths = "items")
    Optional<CartJpaEntity> findWithItemsByMemberId(String memberId);
}
