package io.mallang.product.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {
}
