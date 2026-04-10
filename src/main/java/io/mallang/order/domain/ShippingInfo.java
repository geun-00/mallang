package io.mallang.order.domain;

import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;

public record ShippingInfo(Receiver receiver, Address address) {

    public ShippingInfo {
        if (receiver == null) {
            throw new InvalidValueException("수령인 정보는 필수입니다.");
        }
        if (address == null) {
            throw new InvalidValueException("배송지 주소는 필수입니다.");
        }
    }
}
