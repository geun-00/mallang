package io.mallang.product.adapter.persistence.jpa;

import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements SaveProductPort, LoadProductPort {

    private final ProductJpaRepository productJpaRepository;

    @Override
    @Transactional
    public void save(Product product) {
        productJpaRepository.findById(product.getId().value())
                            .ifPresentOrElse(
                                    entity -> entity.updateFrom(product),
                                    () -> productJpaRepository.save(ProductJpaEntity.from(product))
                            );
    }

    @Override
    public Product getById(ProductId productId) {
        return productJpaRepository.findById(productId.value())
                                   .map(ProductJpaEntity::toDomain)
                                   .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public Product getByIdWithImages(ProductId productId) {
        return productJpaRepository.findWithImagesById(productId.value())
                                   .map(ProductJpaEntity::toDomainWithImages)
                                   .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
