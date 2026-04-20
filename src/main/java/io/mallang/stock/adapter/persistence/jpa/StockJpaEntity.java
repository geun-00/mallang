package io.mallang.stock.adapter.persistence.jpa;

import io.mallang.common.adapter.persistence.jpa.BaseEntity;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.domain.Stock;
import io.mallang.stock.domain.StockQuantity;
import io.mallang.stock.domain.command.RestoreStockCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// TODO : 재고 동시성 제어
@Entity
@Table(name = "stocks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockJpaEntity extends BaseEntity {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    private StockJpaEntity(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    static StockJpaEntity from(Stock stock) {
        return new StockJpaEntity(
                stock.getProductId().value(),
                stock.getQuantity().value()
        );
    }

    Stock toDomain() {
        return Stock.restore(new RestoreStockCommand(
                new ProductId(productId),
                new StockQuantity(quantity)
        ));
    }

    void updateFrom(Stock stock) {
        this.quantity = stock.getQuantity().value();
    }
}
