package io.mallang.product.adapter.persistence.jpa;

import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements SaveProductPort, LoadProductPort {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public void save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.from(product);
        productJpaRepository.save(entity);
    }

    @Override
    public Product getById(ProductId productId) {
        return productJpaRepository.findById(productId.value())
                                   .map(ProductJpaEntity::toDomainWithoutImages)
                                   .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public Product getByIdWithImages(ProductId productId) {
        return productJpaRepository.findWithImagesById(productId.value())
                                   .map(ProductJpaEntity::toDomainWithImages)
                                   .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
