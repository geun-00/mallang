package io.mallang.stock.adapter.persistence.jpa;

import io.mallang.product.domain.ProductId;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.application.required.query.LoadStockForUpdatePort;
import io.mallang.stock.application.required.query.LoadStockPort;
import io.mallang.stock.domain.Stock;
import io.mallang.stock.domain.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Repository
@RequiredArgsConstructor
public class StockPersistenceAdapter implements SaveStockPort,
                                                LoadStockPort,
                                                LoadStockForUpdatePort {

    private final StockJpaRepository stockJpaRepository;

    @Override
    @Transactional
    public void save(Stock stock) {
        stockJpaRepository.findById(stock.getProductId().value())
                          .ifPresentOrElse(
                                  entity -> entity.updateFrom(stock),
                                  () -> stockJpaRepository.save(StockJpaEntity.from(stock))
                          );
    }

    @Override
    public Stock getByProductId(ProductId productId) {
        return toStock(productId, stockJpaRepository.findById(productId.value()));
    }

    @Override
    public List<Stock> getAllByProductIds(List<ProductId> productIds) {
        return toStocks(productIds, stockJpaRepository.findAllById(toIdValues(productIds)));
    }

    @Override
    public Stock getByProductIdForUpdate(ProductId productId) {
        return toStock(productId, stockJpaRepository.findByIdForUpdate(productId.value()));
    }

    @Override
    public List<Stock> getAllByProductIdsForUpdate(List<ProductId> productIds) {
        return toStocks(productIds, stockJpaRepository.findAllByIdForUpdate(toSortedIdValues(productIds)));
    }

    private List<String> toIdValues(List<ProductId> productIds) {
        return productIds.stream()
                         .map(ProductId::value)
                         .toList();
    }

    private List<String> toSortedIdValues(List<ProductId> productIds) {
        return productIds.stream()
                         .map(ProductId::value)
                         .distinct()
                         .sorted()
                         .toList();
    }

    private Stock toStock(ProductId productId, Optional<StockJpaEntity> entity) {
        return entity.map(StockJpaEntity::toDomain)
                     .orElseThrow(() -> new StockNotFoundException(productId));
    }

    private List<Stock> toStocks(List<ProductId> productIds, List<StockJpaEntity> entities) {
        List<Stock> foundStocks = entities.stream()
                                          .map(StockJpaEntity::toDomain)
                                          .toList();

        Set<String> foundProductIds = foundStocks.stream()
                                                 .map(stock -> stock.getProductId().value())
                                                 .collect(toSet());

        validateAllStocksFound(productIds, foundProductIds);

        return foundStocks;
    }

    private void validateAllStocksFound(List<ProductId> targetProductIds, Set<String> foundProductIds) {
        List<ProductId> missingProductIds = targetProductIds.stream()
                                                            .distinct()
                                                            .filter(productId -> !foundProductIds.contains(productId.value()))
                                                            .toList();
        if (missingProductIds.isEmpty()) {
            return;
        }

        throw new StockNotFoundException(missingProductIds);
    }
}
