package io.mallang.test.product.domain;

import io.mallang.DomainTest;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.product.domain.ImageUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("ImageUrl VO")
class ImageUrlTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    void URL은_null_또는_공백이_아니어야_한다(String invalidValue) {
         assertThatThrownBy(() -> new ImageUrl(invalidValue)).isInstanceOf(InvalidValueException.class);
    }

    @ParameterizedTest
    @MethodSource("io.mallang.TestDataSource#invalidImageUrlValues")
    void URL은_유효한_형식이어야_한다(String invalidValue) {
        assertThatThrownBy(() -> new ImageUrl(invalidValue)).isInstanceOf(InvalidValueException.class);
    }
    
    @ParameterizedTest
    @MethodSource("io.mallang.TestDataSource#validImageUrlValues")
    void 유효한_URL은_정상_생성된다(String validValue) {
        assertThatCode(() -> new ImageUrl(validValue)).doesNotThrowAnyException();
    }
}