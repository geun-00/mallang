package io.mallang.member.domain.exception;

import io.mallang.common.domain.exception.DomainNotFoundException;
import io.mallang.member.domain.ShippingAddressId;

public class ShippingAddressNotFoundException extends DomainNotFoundException {

    public ShippingAddressNotFoundException(ShippingAddressId shippingAddressId) {
        super("배송지를 찾을 수 없습니다.", "ShippingAddress를 찾을 수 없습니다 => id: " + shippingAddressId.value());
    }
}
