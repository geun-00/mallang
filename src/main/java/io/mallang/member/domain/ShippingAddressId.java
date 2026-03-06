package io.mallang.member.domain;

public record ShippingAddressId(String value) {

    public ShippingAddressId {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("MemberId는 비어있을 수 없습니다.");
    }
}
