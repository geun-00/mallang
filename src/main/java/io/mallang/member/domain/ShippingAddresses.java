package io.mallang.member.domain;

import io.mallang.domain.common.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShippingAddresses {

    private static final int MAX_SHIPPING_ADDRESSES = 5;

    private final List<ShippingAddress> addresses = new ArrayList<>();

    ShippingAddresses() { }

    ShippingAddress add(AddShippingAddressCommand command, IdGenerator idGenerator) {
        if (addresses.size() >= MAX_SHIPPING_ADDRESSES)
            throw new IllegalStateException("배송지는 최대 5개까지 등록할 수 있습니다.");

        boolean isDefault = addresses.isEmpty();
        ShippingAddress shippingAddress = ShippingAddress.create(command, isDefault, idGenerator);

        addresses.add(shippingAddress);
        return shippingAddress;
    }

    void setDefault(ShippingAddressId shippingAddressId) {
        ShippingAddress newDefault = get(shippingAddressId);
        addresses.forEach(ShippingAddress::unsetDefault);

        newDefault.setDefault();
    }

    ShippingAddress modify(ShippingAddressId id, ModifyShippingAddressCommand command) {
        ShippingAddress target = get(id);
        target.modify(command);

        return target;
    }

    void remove(ShippingAddressId id) {
        ShippingAddress target = get(id);
        addresses.remove(target);
    }

    void restoreAll(List<ShippingAddress> shippingAddresses) {
        addresses.addAll(shippingAddresses);
    }

    private ShippingAddress get(ShippingAddressId id) {
        return addresses.stream()
                        .filter(address -> address.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("해당 배송지 ID를 가진 배송지가 존재하지 않습니다."));
    }

    List<ShippingAddress> toList() {
        return Collections.unmodifiableList(addresses);
    }
}
