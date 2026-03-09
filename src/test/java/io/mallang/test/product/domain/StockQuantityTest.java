package io.mallang.test.product.domain;

import io.mallang.product.domain.StockQuantity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockQuantityTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, -100})
    void 수량은_null_또는_음수가_아니어야_한다(Integer invalidValue) {
        assertThatThrownBy(() -> new StockQuantity(invalidValue)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    void 수량이_0_이상이면_정상_생성된다(Integer validValue) {
        assertThatCode(() -> new StockQuantity(validValue)).doesNotThrowAnyException();
    }
}