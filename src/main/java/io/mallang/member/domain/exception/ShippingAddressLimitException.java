package io.mallang.member.domain.exception;

import io.mallang.domain.common.exception.DomainException;

public class ShippingAddressLimitException extends DomainException {

    public ShippingAddressLimitException() {
        super("배송지는 최대 5개까지 등록할 수 있습니다.");
    }
}
