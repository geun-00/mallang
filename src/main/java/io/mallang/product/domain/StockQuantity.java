package io.mallang.product.domain;

import io.mallang.domain.common.exception.InvalidValueException;

public record StockQuantity(Integer value) {

    public StockQuantity {
        if (value == null || value < 0) {
            throw new InvalidValueException("수량은 null이거나 음수일 수 없습니다.");
        }
    }

    public StockQuantity add(int additionalStock) {
        if (additionalStock <= 0) {
            throw new InvalidValueException("추가 수량은 0보다 커야 합니다.");
        }

        return new StockQuantity(this.value + additionalStock);
    }

    public StockQuantity deduct(int deductedStock) {
        if (this.value < deductedStock) {
            throw new IllegalArgumentException("보유 재고보다 많은 수량을 차감할 수 없습니다.");
        }

        return new StockQuantity(this.value - deductedStock);
    }
}
