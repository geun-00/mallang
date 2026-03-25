package io.mallang.product.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {

    @EntityGraph(attributePaths = "images")
    Optional<ProductJpaEntity> findWithImagesById(String id);
}
