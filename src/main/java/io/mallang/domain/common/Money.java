package io.mallang.domain.common;

import java.math.BigDecimal;

public record Money(BigDecimal value) {

    public static final Money ZERO =  new Money(BigDecimal.ZERO);

    public Money {
        if (value == null)
            throw new InvalidValueException("금액은 null이 될 수 없습니다.");
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidValueException("금액은 음수일 수 없습니다.");
    }

    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }

    public Money multiply(int multiplier) {
        return new Money(this.value.multiply(BigDecimal.valueOf(multiplier)));
    }
}
