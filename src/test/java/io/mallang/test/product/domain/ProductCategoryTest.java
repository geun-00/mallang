package io.mallang.test.product.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.product.domain.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("ProductCategory Enum")
class ProductCategoryTest {

    @Test
    void 유효한_카테고리_문자열로_생성할_수_있다() {
        assertThat(ProductCategory.from("FOOD")).isEqualTo(ProductCategory.FOOD);
    }

    @Test
    void 카테고리_문자열은_대소문자를_구분하지_않는다() {
        assertThat(ProductCategory.from("books")).isEqualTo(ProductCategory.BOOKS);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void 카테고리는_null_또는_공백일_수_없다(String value) {
        assertThatThrownBy(() -> ProductCategory.from(value))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 허용되지_않는_카테고리면_예외가_발생한다() {
        assertThatThrownBy(() -> ProductCategory.from("TOY"))
                .isInstanceOf(InvalidValueException.class);
    }
}
