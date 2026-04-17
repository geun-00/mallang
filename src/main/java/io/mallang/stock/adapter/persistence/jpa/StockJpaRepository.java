package io.mallang.stock.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface StockJpaRepository extends JpaRepository<StockJpaEntity, String> {
}
