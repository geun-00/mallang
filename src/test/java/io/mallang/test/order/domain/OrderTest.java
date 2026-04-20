package io.mallang.test.order.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.common.domain.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderStatus;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.order.domain.command.PlaceOrderItemCommand;
import io.mallang.product.domain.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.mallang.assertions.OrderAssertions.isDerivedFrom;
import static io.mallang.fixtures.CommonFixture.generateClockHolder;
import static io.mallang.fixtures.CommonFixture.generateIdGenerator;
import static io.mallang.fixtures.OrderFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("Order 엔티티")
class OrderTest {

    @Nested
    class 생성 {

        @Test
        void 유효한_정보로_주문을_생성하면_식별자가_할당된다() {
            Order order = generateOrder();

            assertThat(order.getId()).isNotNull();
            assertThat(order.getId().value()).isNotNull();
        }

        @Test
        void 주문을_생성하면_PAYMENT_WAITING_상태가_된다() {
            Order order = generateOrder();

            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_WAITING);
        }

        @Test
        void 주문을_생성하면_주문_시간이_기록된다() {
            ClockHolder clockHolder = generateClockHolder();
            Order order = Order.place(generatePlaceOrderCommand(), generateIdGenerator(), clockHolder);

            assertThat(order.getOrderedAt()).isEqualTo(clockHolder.now());
        }

        @Test
        void 주문을_생성하면_주문자_정보가_저장된다() {
            PlaceOrderCommand command = generatePlaceOrderCommand();
            Order order = Order.place(command, generateIdGenerator(), generateClockHolder());

            assertThat(order.getMemberId()).isEqualTo(command.memberId());
        }

        @Test
        void 주문을_생성하면_배송지_정보가_스냅샷으로_저장된다() {
            PlaceOrderCommand command = generatePlaceOrderCommand();
            Order order = Order.place(command, generateIdGenerator(), generateClockHolder());

            assertThat(order.getShippingInfo()).satisfies(isDerivedFrom(command));
        }

        @Test
        void 주문을_생성하면_주문_상품_목록이_저장된다() {
            List<PlaceOrderItemCommand> itemCommands = generateOrderItemCommands(3);
            Order order = Order.place(generatePlaceOrderCommand(itemCommands), generateIdGenerator(), generateClockHolder());

            assertThat(order.getItems()).hasSize(3);
        }

        @Test
        void 주문을_생성하면_각_주문_상품에_식별자가_할당된다() {
            Order order = generateOrder();

            assertThat(order.getItems())
                    .allSatisfy(item -> {
                        assertThat(item.getId()).isNotNull();
                        assertThat(item.getId().value()).isNotNull();
                    });
        }

        @Test
        void 주문을_생성하면_총_가격은_주문_상품들의_단가와_수량의_합산이다() {
            List<PlaceOrderItemCommand> items = List.of(
                    new PlaceOrderItemCommand(new ProductId("product-1"), 2, new Money(BigDecimal.valueOf(10000))),
                    new PlaceOrderItemCommand(new ProductId("product-2"), 3, new Money(BigDecimal.valueOf(20000)))
            );

            Order order = Order.place(generatePlaceOrderCommand(items), generateIdGenerator(), generateClockHolder());

            assertThat(order.getTotalPrice().value()).isEqualByComparingTo(BigDecimal.valueOf(80000));
        }

        @Test
        void 주문_상품이_없으면_예외가_발생한다() {
            List<PlaceOrderItemCommand> invalidOrderItems = List.of();
            PlaceOrderCommand command = generatePlaceOrderCommand(invalidOrderItems);

            assertThatThrownBy(() -> Order.place(command, generateIdGenerator(), generateClockHolder()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 주문_상품_가격이_0원이면_예외가_발생한다() {
            List<PlaceOrderItemCommand> items = List.of(
                    new PlaceOrderItemCommand(new ProductId("product-1"), 1, new Money(BigDecimal.ZERO))
            );

            assertThatThrownBy(() -> Order.place(generatePlaceOrderCommand(items), generateIdGenerator(), generateClockHolder()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 주문_상품_수량이_0_이하이면_예외가_발생한다() {
            int invalidQuantity = 0;
            List<PlaceOrderItemCommand> items = List.of(
                    new PlaceOrderItemCommand(new ProductId("product-1"), invalidQuantity, new Money(BigDecimal.valueOf(10000)))
            );

            assertThatThrownBy(() -> Order.place(generatePlaceOrderCommand(items), generateIdGenerator(), generateClockHolder()))
                    .isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class 취소 {

        @Test
        void PAYMENT_WAITING_상태에서_주문을_취소하면_CANCELED_상태가_된다() {
            Order order = generateOrder();

            order.cancelBy(order.getMemberId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }

        @Test
        void PREPARING_상태에서_주문을_취소할_수_있다() {
            Order order = generateOrder();
            order.nextStatus(); // PREPARING

            order.cancelBy(order.getMemberId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }

        @Test
        void SHIPPED_상태에서_주문을_취소하면_예외가_발생한다() {
            Order order = generateOrder();
            order.nextStatus(); // PREPARING
            order.nextStatus(); // SHIPPED

            MemberId ordererId = order.getMemberId();
            assertThatThrownBy(() -> order.cancelBy(ordererId)).isInstanceOf(InvalidValueException.class);
        }

        @Test
        void DELIVERING_상태에서_주문을_취소하면_예외가_발생한다() {
            Order order = generateOrder();
            order.nextStatus(); // PREPARING
            order.nextStatus(); // SHIPPED
            order.nextStatus(); // DELIVERING

            MemberId ordererId = order.getMemberId();
            assertThatThrownBy(() -> order.cancelBy(ordererId)).isInstanceOf(InvalidValueException.class);
        }

        @Test
        void DELIVERY_COMPLETED_상태에서_주문을_취소하면_예외가_발생한다() {
            Order order = generateOrder();
            order.nextStatus(); // PREPARING
            order.nextStatus(); // SHIPPED
            order.nextStatus(); // DELIVERING
            order.nextStatus(); // DELIVERY_COMPLETED

            MemberId ordererId = order.getMemberId();
            assertThatThrownBy(() -> order.cancelBy(ordererId)).isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 이미_취소된_주문은_다시_취소할_수_없다() {
            Order order = generateCanceledOrder();

            MemberId ordererId = order.getMemberId();
            assertThatThrownBy(() -> order.cancelBy(ordererId)).isInstanceOf(InvalidValueException.class);
        }
    }
}
