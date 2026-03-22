package io.mallang.member.adapter.persistence.jpa;

import io.mallang.domain.common.vo.Address;
import jakarta.persistence.Embeddable;

@Embeddable
public record AddressJpaVO(String zipCode, String mainAddress, String detailAddress) {

    public static AddressJpaVO from(Address address) {
        return new AddressJpaVO(address.zipCode(), address.mainAddress(), address.detailAddress());
    }

    public Address toDomain() {
        return new Address(zipCode, mainAddress, detailAddress);
    }
}
