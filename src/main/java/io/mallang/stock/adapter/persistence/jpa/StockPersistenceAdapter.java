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
        return stockJpaRepository.findById(productId.value())
                                 .map(StockJpaEntity::toDomain)
                                 .orElseThrow(() -> new StockNotFoundException(productId));
    }

    @Override
    public List<Stock> getAllByProductIds(List<ProductId> productIds) {
        List<Stock> foundStocks = stockJpaRepository.findAllById(toIdValues(productIds))
                                                    .stream()
                                                    .map(StockJpaEntity::toDomain)
                                                    .toList();

        Set<String> foundProductIds = foundStocks.stream()
                                                 .map(stock -> stock.getProductId().value())
                                                 .collect(toSet());

        validateAllStocksFound(productIds, foundProductIds);

        return foundStocks;
    }

    @Override
    public Stock getByProductIdForUpdate(ProductId productId) {
        return stockJpaRepository.findByIdForUpdate(productId.value())
                                 .map(StockJpaEntity::toDomain)
                                 .orElseThrow(() -> new StockNotFoundException(productId));
    }

    @Override
    public List<Stock> getAllByProductIdsForUpdate(List<ProductId> productIds) {
        return List.of();
    }

    private List<String> toIdValues(List<ProductId> productIds) {
        return productIds.stream()
                         .map(ProductId::value)
                         .toList();
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
