package io.mallang.order.adapter.persistence.jpa;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {

    @EntityGraph(attributePaths = "items")
    Optional<OrderJpaEntity> findWithItemsById(String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "items")
    @Query("select o from OrderJpaEntity o where o.id = :id")
    Optional<OrderJpaEntity> findWithItemsByIdForUpdate(@Param("id") String id);
}
