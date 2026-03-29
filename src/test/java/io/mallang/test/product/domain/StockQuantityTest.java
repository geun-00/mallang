package io.mallang.test.product.domain;

import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.product.domain.StockQuantity;
import org.junit.jupiter.api.Test;
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
        assertThatThrownBy(() -> new StockQuantity(invalidValue)).isInstanceOf(InvalidValueException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    void 수량이_0_이상이면_정상_생성된다(Integer validValue) {
        assertThatCode(() -> new StockQuantity(validValue)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 추가_수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
        StockQuantity stock = new StockQuantity(10);

        assertThatThrownBy(() -> stock.add(invalidQuantity))
                .isInstanceOf(InvalidValueException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 확인_수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
        StockQuantity stock = new StockQuantity(10);

        assertThatThrownBy(() -> stock.checkAvailable(invalidQuantity))
                .isInstanceOf(InvalidValueException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 차감_수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
        StockQuantity stock = new StockQuantity(10);

        assertThatThrownBy(() -> stock.deduct(invalidQuantity))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 확인_대상_수량이_보유_재고보다_많으면_예외가_발생한다() {
        StockQuantity stock = new StockQuantity(2);

        assertThatThrownBy(() -> stock.checkAvailable(3))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 차감_결과가_음수이면_예외가_발생한다() {
        StockQuantity stock = new StockQuantity(2);

        assertThatThrownBy(() -> stock.deduct(3))
                .isInstanceOf(InvalidValueException.class);
    }
}
