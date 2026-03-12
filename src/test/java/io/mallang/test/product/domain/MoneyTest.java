package io.mallang.test.product.domain;

import io.mallang.domain.common.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void 두_금액을_더하면_합산된_금액이_반환된다() {
        Money money1 = new Money(BigDecimal.valueOf(10000));
        Money money2 = new Money(BigDecimal.valueOf(20000));

        Money result = money1.add(money2);

        assertThat(result.value()).isEqualByComparingTo(BigDecimal.valueOf(30000));
    }

    @Test
    void 금액을_더해도_원본_객체는_변하지_않는다() {
        Money money1 = new Money(BigDecimal.valueOf(10000));
        Money money2 = new Money(BigDecimal.valueOf(20000));

        money1.add(money2);

        assertThat(money1.value()).isEqualByComparingTo(BigDecimal.valueOf(10000));
    }

    @Test
    void 금액에_수량을_곱하면_곱산된_금액이_반환된다() {
        Money price = new Money(BigDecimal.valueOf(10000));

        Money result = price.multiply(3);

        assertThat(result.value()).isEqualByComparingTo(BigDecimal.valueOf(30000));
    }

    @Test
    void 금액을_곱해도_원본_객체는_변하지_않는다() {
        Money price = new Money(BigDecimal.valueOf(10000));

        price.multiply(3);

        assertThat(price.value()).isEqualByComparingTo(BigDecimal.valueOf(10000));
    }
}