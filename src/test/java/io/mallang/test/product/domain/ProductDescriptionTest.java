package io.mallang.test.product.domain;

import io.mallang.product.domain.ProductDescription;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductDescriptionTest {

    @Test
    void 상품_설명은_null이_아니여야_한다() {
        assertThatThrownBy(() -> new ProductDescription(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 상품_설명은_2000자_이하여야_한다() {
        String longDescription = "a".repeat(2001);

        assertThatThrownBy(() -> new ProductDescription(longDescription))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void 상품_설명이_공백이면_빈_문자열로_생성된다() {
        ProductDescription description = new ProductDescription("   ");

        assertThat(description.value().isBlank()).isTrue();
        assertThat(description.value()).isEqualTo("");
    }
}