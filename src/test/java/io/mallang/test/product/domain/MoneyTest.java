package io.mallang.test.product.domain;

import io.mallang.product.domain.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void 금액이_null이_아니어야_한다() {
        assertThatThrownBy(() -> new Money(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    void 금액은_음수가_아니어야_한다(int invalidValue) {
        assertThatThrownBy(() -> new Money(BigDecimal.valueOf(invalidValue))).isInstanceOf(IllegalArgumentException.class);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    void 금액이_0_이상이면_정상_생성된다(int validValue) {
        assertThatCode(() -> new Money(BigDecimal.valueOf(validValue))).doesNotThrowAnyException();
    }
}