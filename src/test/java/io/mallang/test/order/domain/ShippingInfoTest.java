package io.mallang.test.order.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.order.domain.ShippingInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("ShippingInfo VO")
class ShippingInfoTest {

    @Test
    void 수령인과_주소로_배송정보를_생성할_수_있다() {
        Receiver receiver = new Receiver("홍길동", "01012345678");
        Address address = new Address("12345", "서울시 강남구 테헤란로 1", "101호");

        assertThatCode(() -> new ShippingInfo(receiver, address))
                .doesNotThrowAnyException();
    }

    @Test
    void 수령인이_null이면_예외가_발생한다() {
        Address address = new Address("12345", "서울시 강남구 테헤란로 1", "101호");

        assertThatThrownBy(() -> new ShippingInfo(null, address))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 주소가_null이면_예외가_발생한다() {
        Receiver receiver = new Receiver("홍길동", "01012345678");

        assertThatThrownBy(() -> new ShippingInfo(receiver, null))
                .isInstanceOf(InvalidValueException.class);
    }
}
