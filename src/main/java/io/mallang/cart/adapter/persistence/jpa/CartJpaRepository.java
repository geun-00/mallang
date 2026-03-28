package io.mallang.cart.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartJpaRepository extends JpaRepository<CartJpaEntity, String> {
}
