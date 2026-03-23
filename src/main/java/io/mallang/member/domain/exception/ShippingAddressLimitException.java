package io.mallang.member.domain.exception;

public class ShippingAddressLimitException extends RuntimeException {

    public ShippingAddressLimitException() {
        super("배송지는 최대 5개까지 등록할 수 있습니다.");
    }
}
