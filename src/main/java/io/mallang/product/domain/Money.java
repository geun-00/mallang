package io.mallang.product.domain;

import java.math.BigDecimal;

public record Money(BigDecimal value) {

    public Money {
        if (value == null)
            throw new IllegalArgumentException("금액은 null이 될 수 없습니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("금액은 음수일 수 없습니다.");
    }
}
