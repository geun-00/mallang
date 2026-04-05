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
        List<Product> foundProducts = productJpaRepository.findAllById(toIdValues(productIds))
                                                          .stream()
                                                          .map(ProductJpaEntity::toDomain)
                                                          .toList();

        Set<String> foundProductIds = foundProducts.stream()
                                                   .map(product -> product.getId().value())
                                                   .collect(toSet());

        validateAllProductsFound(productIds, foundProductIds);

        return foundProducts;
    }

    private List<String> toIdValues(List<ProductId> productIds) {
        return productIds.stream()
                         .map(ProductId::value)
                         .toList();
    }

    private void validateAllProductsFound(List<ProductId> targetProductIds, Set<String> foundProductIds) {
        if (targetProductIds.size() == foundProductIds.size()) {
            return;
        }

        List<ProductId> missingProductIds = targetProductIds.stream()
                                                            .filter(productId -> !foundProductIds.contains(productId.value()))
                                                            .toList();

        throw new ProductNotFoundException(missingProductIds);
    }
}
