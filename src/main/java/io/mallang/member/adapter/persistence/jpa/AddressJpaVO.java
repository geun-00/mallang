package io.mallang.member.adapter.persistence.jpa;

import io.mallang.common.domain.vo.Address;
import jakarta.persistence.Embeddable;

@Embeddable
record AddressJpaVO(String zipCode, String mainAddress, String detailAddress) {

    static AddressJpaVO from(Address address) {
        return new AddressJpaVO(address.zipCode(), address.mainAddress(), address.detailAddress());
    }

    Address toDomain() {
        return new Address(zipCode, mainAddress, detailAddress);
    }
}
