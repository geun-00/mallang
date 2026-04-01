package io.mallang.product.adapter.persistence.jpa;

import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

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

    @Override
    public List<Product> getAllByIds(List<ProductId> productIds) {
        List<String> targetIds = productIds.stream()
                                           .map(ProductId::value)
                                           .toList();

        List<Product> foundProducts = productJpaRepository.findAllById(targetIds)
                                                          .stream()
                                                          .map(ProductJpaEntity::toDomain)
                                                          .toList();

        if (foundProducts.size() != targetIds.size()) {
            Set<ProductId> foundIds = foundProducts.stream()
                                                   .map(Product::getId)
                                                   .collect(toSet());

            List<ProductId> missingProductIds = productIds.stream()
                                                          .filter(productId -> !foundIds.contains(productId))
                                                          .toList();

            throw new ProductNotFoundException(missingProductIds);
        }

        return foundProducts;
    }
}
