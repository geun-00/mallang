package io.mallang.stock.adapter.persistence.jpa;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface StockJpaRepository extends JpaRepository<StockJpaEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockJpaEntity s where s.productId = :productId")
    Optional<StockJpaEntity> findByIdForUpdate(String productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s
            from StockJpaEntity s
            where s.productId in :productIds
            order by s.productId
            """)
    List<StockJpaEntity> findAllByIdForUpdate(List<String> productIds);
}
