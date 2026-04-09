package io.mallang.member.domain;

import io.mallang.common.domain.port.IdGenerator;
import io.mallang.member.domain.command.AddShippingAddressCommand;
import io.mallang.member.domain.command.ModifyShippingAddressCommand;
import io.mallang.member.domain.exception.ShippingAddressLimitException;
import io.mallang.member.domain.exception.ShippingAddressNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShippingAddresses {

    private static final int MAX_SHIPPING_ADDRESSES = 5;

    private final List<ShippingAddress> addresses = new ArrayList<>();

    ShippingAddresses() { }

    ShippingAddress add(AddShippingAddressCommand command, IdGenerator idGenerator) {
        if (addresses.size() >= MAX_SHIPPING_ADDRESSES) {
            throw new ShippingAddressLimitException();
        }

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
                        .orElseThrow(() -> new ShippingAddressNotFoundException(id));
    }

    List<ShippingAddress> toList() {
        return Collections.unmodifiableList(addresses);
    }
}
