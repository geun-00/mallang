package io.mallang.test.stock.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.stock.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static io.mallang.fixtures.StockFixture.generateStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("Stock 엔티티")
class StockTest {

    @Nested
    class 생성 {

        @ParameterizedTest
        @NullSource
        @ValueSource(ints = {-1, -100})
        void 수량은_null_또는_음수가_아니어야_한다(Integer invalidValue) {
            assertThatThrownBy(() -> generateStock(invalidValue))
                    .isInstanceOf(InvalidValueException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 100})
        void 수량이_0_이상이면_정상_생성된다(Integer validValue) {
            assertThatCode(() -> generateStock(validValue)).doesNotThrowAnyException();
        }
    }

    @Nested
    class 증가 {

        @Test
        void 재고를_추가하면_수량이_증가한다() {
            Stock stock = generateStock(10);

            stock.add(3);

            assertThat(stock.getQuantity().value()).isEqualTo(13);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void 추가_수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
            Stock stock = generateStock(10);

            assertThatThrownBy(() -> stock.add(invalidQuantity))
                    .isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class 차감 {

        @Test
        void 재고를_차감하면_수량이_감소한다() {
            Stock stock = generateStock(10);

            stock.deduct(3);

            assertThat(stock.getQuantity().value()).isEqualTo(7);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void 차감_수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
            Stock stock = generateStock(10);

            assertThatThrownBy(() -> stock.deduct(invalidQuantity))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 차감_결과가_음수이면_예외가_발생한다() {
            Stock stock = generateStock(2);

            assertThatThrownBy(() -> stock.deduct(3))
                    .isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class 확인 {

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void 확인_수량이_0_이하이면_예외가_발생한다(int invalidQuantity) {
            Stock stock = generateStock(10);

            assertThatThrownBy(() -> stock.checkAvailable(invalidQuantity))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 확인_대상_수량이_보유_재고보다_많으면_예외가_발생한다() {
            Stock stock = generateStock(2);

            assertThatThrownBy(() -> stock.checkAvailable(3))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 충분한_재고가_있으면_예외가_발생하지_않는다() {
            Stock stock = generateStock(10);

            assertThatCode(() -> stock.checkAvailable(3)).doesNotThrowAnyException();
        }
    }

}
