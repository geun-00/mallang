package io.mallang.product.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public record StockQuantity(Integer value) {

    public StockQuantity {
        if (value == null || value < 0) {
            throw new InvalidValueException("수량은 null이거나 음수일 수 없습니다.");
        }
    }

    public StockQuantity add(int additionalStock) {
        validatePositive(additionalStock, "추가 수량은 0보다 커야 합니다.");

        return new StockQuantity(this.value + additionalStock);
    }

    public StockQuantity deduct(int deductedStock) {
        checkAvailable(deductedStock);

        return new StockQuantity(this.value - deductedStock);
    }

    public void checkAvailable(int quantity) {
        validatePositive(quantity, "확인 수량은 0보다 커야 합니다.");

        if (this.value < quantity) {
            throw new InvalidValueException("보유 재고보다 많은 수량을 차감할 수 없습니다.");
        }
    }

    private void validatePositive(int quantity, String message) {
        if (quantity <= 0) {
            throw new InvalidValueException(message);
        }
    }
}
