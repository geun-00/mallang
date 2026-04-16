package io.mallang.assertions;

import io.mallang.common.application.query.SliceResult;
import io.mallang.common.domain.vo.Money;
import io.mallang.order.adapter.web.model.OrderDetailResponse;
import io.mallang.order.adapter.web.model.SearchMyOrdersResponse;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderItem;
import io.mallang.order.domain.ShippingInfo;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.product.domain.Product;
import org.assertj.core.api.ThrowingConsumer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.mallang.order.adapter.web.model.SearchMyOrdersResponse.OrderSummary;
import static io.mallang.order.adapter.web.model.OrderDetailResponse.OrderItemResponse;
import static io.mallang.order.application.provided.query.model.OrderDetailView.OrderItemView;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderAssertions {

    public static ThrowingConsumer<ShippingInfo> isDerivedFrom(PlaceOrderCommand command) {
        return shippingInfo -> {
            assertThat(shippingInfo.receiver()).isEqualTo(command.receiver());
            assertThat(shippingInfo.address()).isEqualTo(command.address());
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

    public static ThrowingConsumer<OrderListView> isSummaryOf(Order order, Product mainProduct, int quantity) {
        return item -> {
            assertThat(item.orderId()).isEqualTo(order.getId().value());
            assertThat(item.status()).isEqualTo(order.getStatus().name());
            assertThat(item.orderedAt()).isEqualTo(order.getOrderedAt());
            assertThat(item.totalPrice()).isEqualByComparingTo(mainProduct.getPrice().multiply(quantity).value());
            assertThat(item.itemCount()).isEqualTo(order.getItems().size());
            assertThat(item.mainProductId()).isEqualTo(mainProduct.getId().value());
            assertThat(item.mainProductName()).isEqualTo(mainProduct.getName().value());
            assertThat(item.mainProductThumbnailImageUrl()).isEqualTo(mainProduct.getThumbnailImage().imageUrl().value());
        };
    }

    public static ThrowingConsumer<OrderDetailView> isDetailOf(Order order, Product product, int quantity) {
        return detail -> {
            assertThat(detail.orderId()).isEqualTo(order.getId().value());
            assertThat(detail.memberId()).isEqualTo(order.getMemberId().value());
            assertThat(detail.status()).isEqualTo(order.getStatus().name());
            assertThat(detail.orderedAt()).isEqualTo(order.getOrderedAt());
            assertThat(detail.totalPrice()).isEqualByComparingTo(product.getPrice().multiply(quantity).value());
            assertThat(detail.receiverName()).isEqualTo(order.getShippingInfo().receiver().name());
            assertThat(detail.receiverPhoneNumber()).isEqualTo(order.getShippingInfo().receiver().phoneNumber());
            assertThat(detail.zipCode()).isEqualTo(order.getShippingInfo().address().zipCode());
            assertThat(detail.mainAddress()).isEqualTo(order.getShippingInfo().address().mainAddress());
            assertThat(detail.detailAddress()).isEqualTo(order.getShippingInfo().address().detailAddress());
            assertThat(detail.items()).hasSize(order.getItems().size());

            OrderItem orderItem = order.getItems().getFirst();
            OrderItemView item = detail.items().getFirst();
            assertThat(item.orderItemId()).isEqualTo(orderItem.getId().value());
            assertThat(item.productId()).isEqualTo(product.getId().value());
            assertThat(item.productName()).isEqualTo(product.getName().value());
            assertThat(item.productThumbnailImageUrl()).isEqualTo(product.getThumbnailImage().imageUrl().value());
            assertThat(item.price()).isEqualByComparingTo(product.getPrice().value());
            assertThat(item.quantity()).isEqualTo(quantity);
            assertThat(item.totalPrice()).isEqualByComparingTo(product.getPrice().multiply(quantity).value());
        };
    }

    public static ThrowingConsumer<SearchMyOrdersResponse> isMappedFrom(SliceResult<OrderListView> result) {
        return response -> {
            assertThat(response.items()).hasSize(result.items().size());

            for (int i = 0; i < result.items().size(); i++) {
                OrderListView item = result.items().get(i);
                OrderSummary summary = response.items().get(i);

                assertThat(summary.orderId()).isEqualTo(item.orderId());
                assertThat(summary.status()).isEqualTo(item.status());
                assertThat(summary.orderedAt()).isEqualTo(item.orderedAt());
                assertThat(summary.totalPrice()).isEqualTo(item.totalPrice());
                assertThat(summary.itemCount()).isEqualTo(item.itemCount());
                assertThat(summary.mainProductId()).isEqualTo(item.mainProductId());
                assertThat(summary.mainProductName()).isEqualTo(item.mainProductName());
                assertThat(summary.mainProductThumbnailImageUrl()).isEqualTo(item.mainProductThumbnailImageUrl());
            }

            assertThat(response.hasNext()).isEqualTo(result.hasNext());
            assertThat(response.nextCursor()).isEqualTo(result.nextCursor());
        };
    }

    public static ThrowingConsumer<OrderDetailResponse> isMappedFrom(OrderDetailView view) {
        return response -> {
            assertThat(response.orderId()).isEqualTo(view.orderId());
            assertThat(response.memberId()).isEqualTo(view.memberId());
            assertThat(response.status()).isEqualTo(view.status());
            assertThat(response.orderedAt()).isEqualTo(view.orderedAt());
            assertThat(response.totalPrice()).isEqualTo(view.totalPrice());
            assertThat(response.receiverName()).isEqualTo(view.receiverName());
            assertThat(response.receiverPhoneNumber()).isEqualTo(view.receiverPhoneNumber());
            assertThat(response.zipCode()).isEqualTo(view.zipCode());
            assertThat(response.mainAddress()).isEqualTo(view.mainAddress());
            assertThat(response.detailAddress()).isEqualTo(view.detailAddress());
            assertThat(response.items()).hasSize(view.items().size());

            for (int i = 0; i < view.items().size(); i++) {
                OrderItemView item = view.items().get(i);
                OrderItemResponse itemResponse = response.items().get(i);

                assertThat(itemResponse.orderItemId()).isEqualTo(item.orderItemId());
                assertThat(itemResponse.productId()).isEqualTo(item.productId());
                assertThat(itemResponse.productName()).isEqualTo(item.productName());
                assertThat(itemResponse.productThumbnailImageUrl()).isEqualTo(item.productThumbnailImageUrl());
                assertThat(itemResponse.price()).isEqualTo(item.price());
                assertThat(itemResponse.quantity()).isEqualTo(item.quantity());
                assertThat(itemResponse.totalPrice()).isEqualTo(item.totalPrice());
            }
        };
    }
}
