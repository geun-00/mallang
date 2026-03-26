package io.mallang;

import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderItem;
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

    public static ThrowingConsumer<Order> isSameAs(Order expected) {
        return actual -> {
            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getMemberId()).isEqualTo(expected.getMemberId());
            assertThat(actual.getTotalPrice().value()).isEqualByComparingTo(expected.getTotalPrice().value());
            assertThat(actual.getShippingInfo()).isEqualTo(expected.getShippingInfo());
            assertThat(actual.getOrderedAt()).isEqualTo(expected.getOrderedAt());
            assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
            assertThat(actual.getItems()).hasSize(expected.getItems().size());

            for (int i = 0; i < expected.getItems().size(); i++) {
                assertThat(actual.getItems().get(i)).satisfies(isSameAs(expected.getItems().get(i)));
            }

        };
    }

    public static ThrowingConsumer<OrderItem> isSameAs(OrderItem expected) {
        return actual -> {
            assertThat(actual.getId()).isEqualTo(expected.getId());
            assertThat(actual.getProductId()).isEqualTo(expected.getProductId());
            assertThat(actual.getQuantity()).isEqualTo(expected.getQuantity());
            assertThat(actual.getPrice().value()).isEqualByComparingTo(expected.getPrice().value());
        };
    }

    public static ThrowingConsumer<OrderItem> isDerivedFrom(String productId, int quantity) {
        return orderItem -> {
            assertThat(orderItem.getProductId().value()).isEqualTo(productId);
            assertThat(orderItem.getQuantity()).isEqualTo(quantity);
        };
    }
}
