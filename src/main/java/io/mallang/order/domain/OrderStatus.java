package io.mallang.order.domain;

import io.mallang.common.domain.exception.InvalidValueException;

public enum OrderStatus {
    PAYMENT_WAITING,
    PREPARING,
    SHIPPED,
    DELIVERING,
    DELIVERY_COMPLETED,
    CANCELED;

    boolean isCancelable() {
        return this == PAYMENT_WAITING || this == PREPARING;
    }

    OrderStatus next() {
        return switch (this) {
            case PAYMENT_WAITING -> PREPARING;
            case PREPARING -> SHIPPED;
            case SHIPPED -> DELIVERING;
            case DELIVERING -> DELIVERY_COMPLETED;
            default -> throw new InvalidValueException("다음 상태로 전환할 수 없습니다.");
        };
    }
}
