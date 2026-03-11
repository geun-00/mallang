package io.mallang.product.domain;

public record StockQuantity(Integer value) {

    public StockQuantity {
        if (value == null || value < 0)
            throw new IllegalArgumentException("수량은 null이거나 음수일 수 없습니다.");
    }

    public StockQuantity add(int additionalStock) {
        return new StockQuantity(this.value + additionalStock);
    }

    public StockQuantity deduct(int deductedStock) {
        if (this.value < deductedStock)
            throw new IllegalArgumentException("보유 재고보다 많은 수량을 차감할 수 없습니다.");

        return new StockQuantity(this.value - deductedStock);
    }
}
