package io.mallang.test.common;

import io.mallang.annotations.DomainTest;
import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.exception.InvalidValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("Address VO")
class AddressTest {

    @Test
    void 유효한_정보로_주소를_생성할_수_있다() {
        assertThatCode(() -> new Address("12345", "서울시 강남구 테헤란로 1", "101호"))
                .doesNotThrowAnyException();
    }

    @Test
    void 상세주소_없이_주소를_생성할_수_있다() {
        assertThatCode(() -> new Address("12345", "서울시 강남구 테헤란로 1", null))
                .doesNotThrowAnyException();
    }

    @Test
    void 우편번호는_null이면_안_된다() {
        assertThatThrownBy(() -> new Address(null, "서울시 강남구 테헤란로 1", "101호"))
                .isInstanceOf(InvalidValueException.class);
    }

    @ParameterizedTest
    @MethodSource("io.mallang.TestDataSource#invalidZipcodeValues")
    void 우편번호는_5자리_숫자여야_한다(String invalidZipcode) {
        assertThatThrownBy(() -> new Address(invalidZipcode, "서울시 강남구 테헤란로 1", "101호"))
                .isInstanceOf(InvalidValueException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    void 주소는_null_또는_공백이_아니어야_한다(String invalidMainAddress) {
        assertThatThrownBy(() -> new Address("12345", invalidMainAddress, "101호"))
                .isInstanceOf(InvalidValueException.class);
    }
}
