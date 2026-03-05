package io.mallang.test.member.domain;

import io.mallang.member.domain.Address;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 우편번호는_5자리_숫자여야_한다() {
        assertThatThrownBy(() -> new Address("1234", "서울시 강남구 테헤란로 1", "101호"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Address("123456", "서울시 강남구 테헤란로 1", "101호"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Address("1234a", "서울시 강남구 테헤란로 1", "101호"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주소는_null_또는_공백이_아니어야_한다() {
        assertThatThrownBy(() -> new Address("12345", null, "101호"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Address("12345", "  ", "101호"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
