package io.mallang.test.product.domain;

import io.mallang.product.domain.ProductName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductNameTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    void 상품명은_null_또는_공백이_아니어야_한다(String invalidName) {
        assertThatThrownBy(() -> new ProductName(invalidName)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {" test", "test "})
    void 상품명은_앞뒤_공백이_없어야_한다(String invalidName) {
        assertThatThrownBy(() -> new ProductName(invalidName)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 상품명은_100자_이하여야_한다() {
        String longName = "a".repeat(101);
        assertThatThrownBy(() -> new ProductName(longName)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품명은_정확히_100자이면_정상_생성된다() {
        String boundaryName = "a".repeat(100);
        assertThatCode(() -> new ProductName(boundaryName)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"상품명", "Valid Product Name 123", "상품명!@#$%^&*()"})
    void 유효한_형식으로_상품명을_생성할_수_있다(String validName) {
        assertThatCode(() -> new ProductName(validName)).doesNotThrowAnyException();
    }
}