package io.mallang;

import io.mallang.domain.common.Address;
import io.mallang.domain.common.Receiver;
import io.mallang.order.domain.PlaceOrderCommand;
import io.mallang.order.domain.ShippingInfo;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderAssertions {

    public static ThrowingConsumer<ShippingInfo> isDerivedFrom(PlaceOrderCommand command) {
        return shippingInfo -> {
            assertThat(shippingInfo.receiver()).isEqualTo(new Receiver(command.receiverName(), command.receiverPhoneNumber()));
            assertThat(shippingInfo.address()).isEqualTo(new Address(command.zipCode(), command.mainAddress(), command.detailAddress()));
        };
    }
}
