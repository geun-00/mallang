package io.mallang;

import io.mallang.order.domain.PlaceOrderCommand;
import io.mallang.order.domain.ShippingInfo;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderAssertions {

    public static ThrowingConsumer<ShippingInfo> isDerivedFrom(PlaceOrderCommand command) {
        return shippingInfo -> {

            assertThat(shippingInfo.receiver()).satisfies(receiver -> {
                assertThat(receiver.name()).isEqualTo(command.receiverName());
                assertThat(receiver.phoneNumber()).isEqualTo(command.receiverPhoneNumber());
            });

            assertThat(shippingInfo.address()).satisfies(address -> {
                assertThat(address.zipCode()).isEqualTo(command.zipCode());
                assertThat(address.mainAddress()).isEqualTo(command.mainAddress());
                assertThat(address.detailAddress()).isEqualTo(command.detailAddress());
            });
        };
    }
}
