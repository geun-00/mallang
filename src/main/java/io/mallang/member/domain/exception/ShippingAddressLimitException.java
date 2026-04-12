package io.mallang.member.domain.exception;

import io.mallang.common.domain.exception.DomainException;

public class ShippingAddressLimitException extends DomainException {

    public ShippingAddressLimitException() {
        super("배송지는 최대 5개까지 등록할 수 있습니다.", "배송지 등록 한도를 초과했습니다.");
    }

    public ShippingAddressLimitException(int currentCount, int limit) {
        super(
                "배송지는 최대 5개까지 등록할 수 있습니다.",
                "배송지 등록 한도를 초과했습니다 => %d/%d".formatted(currentCount, limit)
        );
    }
}
