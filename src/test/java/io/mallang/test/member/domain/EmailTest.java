package io.mallang.test.member.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.member.domain.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("Email VO")
class EmailTest {
    
    @ParameterizedTest
    @MethodSource("io.mallang.TestDataSource#invalidEmailValues")
    void 유효하지_않은_형식으로_생성할_수_없다(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                 .isInstanceOf(InvalidValueException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    void 이메일은_null이나_빈_문자열이_될_수_없다(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(InvalidValueException.class);
    }
    
    @ParameterizedTest
    @MethodSource("io.mallang.TestDataSource#validEmailValues")
    void 유효한_이메일_형식으로_생성할_수_있다(String validEmail) {
        assertThatCode(() -> new Email(validEmail)).doesNotThrowAnyException();
    }
}