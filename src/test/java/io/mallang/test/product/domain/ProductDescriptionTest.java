package io.mallang.test.product.domain;

import io.mallang.DomainTest;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.product.domain.ProductDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("ProductDescription VO")
class ProductDescriptionTest {

    @Test
    void 상품_설명은_null이_아니여야_한다() {
        assertThatThrownBy(() -> new ProductDescription(null))
                .isInstanceOf(InvalidValueException.class);
    }
    
    @Test
    void 상품_설명은_2000자_이하여야_한다() {
        String longDescription = "a".repeat(2001);

        assertThatThrownBy(() -> new ProductDescription(longDescription))
                .isInstanceOf(InvalidValueException.class);
    }
    
    @Test
    void 상품_설명이_공백이면_빈_문자열로_생성된다() {
        ProductDescription description = new ProductDescription("   ");

        assertThat(description.value()).isBlank();
        assertThat(description.value()).isEmpty();
    }

    @Test
    void 상품_설명의_앞뒤_공백은_제거된다() {
        ProductDescription description = new ProductDescription("  설명  ");
        assertThat(description.value()).isEqualTo("설명");
    }
}