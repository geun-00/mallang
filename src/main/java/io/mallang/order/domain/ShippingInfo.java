package io.mallang.order.domain;

import io.mallang.domain.common.Address;
import io.mallang.domain.common.Receiver;

public record ShippingInfo(Receiver receiver, Address address) {

    public ShippingInfo {
        if (receiver == null)
            throw new IllegalArgumentException("수령인 정보는 필수입니다.");
        if (address == null)
            throw new IllegalArgumentException("배송지 주소는 필수입니다.");
    }
}
