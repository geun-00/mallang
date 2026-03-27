package io.mallang.order.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {

    @EntityGraph(attributePaths = "items")
    Optional<OrderJpaEntity> findWithItemsById(String id);
}
