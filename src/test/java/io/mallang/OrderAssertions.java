package io.mallang;

import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Money;
import io.mallang.domain.common.vo.Receiver;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.product.domain.Product;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderItem;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.order.domain.ShippingInfo;
import org.assertj.core.api.ThrowingConsumer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class OrderAssertions {

    public static ThrowingConsumer<ShippingInfo> isDerivedFrom(PlaceOrderCommand command) {
        return shippingInfo -> {
            assertThat(shippingInfo.receiver()).isEqualTo(new Receiver(command.receiverName(), command.receiverPhoneNumber()));
            assertThat(shippingInfo.address()).isEqualTo(new Address(command.zipCode(), command.mainAddress(), command.detailAddress()));
        };
    }

    public static ThrowingConsumer<Order> isCreatedFrom(CreateOrderCommand command) {
        return order -> {
            assertThat(order.getMemberId().value()).isEqualTo(command.memberIdValue());
            assertThat(order.getStatus().name()).isEqualTo("PAYMENT_WAITING");
            assertThat(order.getItems()).hasSize(command.items().size());
            assertThat(order.getShippingInfo().receiver().name()).isEqualTo(command.receiverName());
            assertThat(order.getShippingInfo().receiver().phoneNumber()).isEqualTo(command.receiverPhoneNumber());
            assertThat(order.getShippingInfo().address().zipCode()).isEqualTo(command.zipCode());
            assertThat(order.getShippingInfo().address().mainAddress()).isEqualTo(command.mainAddress());
            assertThat(order.getShippingInfo().address().detailAddress()).isEqualTo(command.detailAddress());
        };
    }

    public static ThrowingConsumer<Order> isCreatedFrom(CreateOrderCommand command, List<Product> products) {
        return order -> {
            assertThat(order).satisfies(isCreatedFrom(command));

            Map<String, Product> productsById = products.stream()
                                                        .collect(toMap(product -> product.getId().value(), identity()));

            Money expectedTotalPrice = Money.ZERO;
            assertThat(order.getItems()).hasSize(command.items().size());

            for (int i = 0; i < command.items().size(); i++) {
                var requestedItem = command.items().get(i);
                Product product = productsById.get(requestedItem.productId());

                assertThat(product).isNotNull();
                assertThat(order.getItems().get(i)).satisfies(
                        isDerivedFrom(product.getId().value(), requestedItem.quantity(), product.getPrice().value())
                );

                expectedTotalPrice = expectedTotalPrice.add(product.getPrice().multiply(requestedItem.quantity()));
            }

            assertThat(order.getTotalPrice().value()).isEqualByComparingTo(expectedTotalPrice.value());
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

    public static ThrowingConsumer<OrderItem> isDerivedFrom(String productId, int quantity, BigDecimal price) {
        return orderItem -> {
            assertThat(orderItem).satisfies(isDerivedFrom(productId, quantity));
            assertThat(orderItem.getPrice().value()).isEqualByComparingTo(price);
        };
    }
}
