package io.mallang.test.order.domain;

import io.mallang.domain.common.ClockHolder;
import io.mallang.member.domain.MemberId;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.PlaceOrderItemCommand;
import io.mallang.order.domain.OrderStatus;
import io.mallang.order.domain.PlaceOrderCommand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.mallang.OrderAssertions.isDerivedFrom;
import static io.mallang.fixtures.OrderFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

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

        assertThat(order.getMemberId().value()).isEqualTo(command.memberId());
    }

    @Test
    void 주문자인지_확인할_수_있다() {
        Order order = generateOrder();

        assertThat(order.isOrderer(order.getMemberId())).isTrue();
        assertThat(order.isOrderer(new MemberId("other-member-id"))).isFalse();
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
        // given
        List<PlaceOrderItemCommand> items = List.of(
                new PlaceOrderItemCommand("product-1", 2, BigDecimal.valueOf(10000)),
                new PlaceOrderItemCommand("product-2", 3, BigDecimal.valueOf(20000))
        );

        // when
        Order order = Order.place(generatePlaceOrderCommand(items), generateIdGenerator(), generateClockHolder());

        // then
        assertThat(order.getTotalPrice().value()).isEqualByComparingTo(BigDecimal.valueOf(80000));
    }

    @Test
    void 주문_상품이_없으면_예외가_발생한다() {
        List<PlaceOrderItemCommand> invalidOrderItems = List.of();
        PlaceOrderCommand command = generatePlaceOrderCommand(invalidOrderItems);

        assertThatThrownBy(() -> Order.place(command, generateIdGenerator(), generateClockHolder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_상품_가격이_0원이면_예외가_발생한다() {
        List<PlaceOrderItemCommand> items = List.of(new PlaceOrderItemCommand("product-1", 1, BigDecimal.ZERO));

        assertThatThrownBy(() -> Order.place(generatePlaceOrderCommand(items), generateIdGenerator(), generateClockHolder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_상품_수량이_0_이하이면_예외가_발생한다() {
        int invalidQuantity = 0;
        List<PlaceOrderItemCommand> items = List.of(new PlaceOrderItemCommand("product-1", invalidQuantity, BigDecimal.valueOf(10000)));

        assertThatThrownBy(() -> Order.place(generatePlaceOrderCommand(items), generateIdGenerator(), generateClockHolder()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void PAYMENT_WAITING_상태에서_주문을_취소하면_CANCELED_상태가_된다() {
        Order order = generateOrder();

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void PREPARING_상태에서_주문을_취소할_수_있다() {
        Order order = generateOrder();
        order.nextStatus(); // PREPARING

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void SHIPPED_상태에서_주문을_취소하면_예외가_발생한다() {
        Order order = generateOrder();
        order.nextStatus(); // PREPARING
        order.nextStatus(); // SHIPPED

        assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void DELIVERING_상태에서_주문을_취소하면_예외가_발생한다() {
        Order order = generateOrder();
        order.nextStatus(); // PREPARING
        order.nextStatus(); // SHIPPED
        order.nextStatus(); // DELIVERING

        assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void DELIVERY_COMPLETED_상태에서_주문을_취소하면_예외가_발생한다() {
        Order order = generateOrder();
        order.nextStatus(); // PREPARING
        order.nextStatus(); // SHIPPED
        order.nextStatus(); // DELIVERING
        order.nextStatus(); // DELIVERY_COMPLETED

        assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 이미_취소된_주문은_다시_취소할_수_없다() {
        Order order = generateCanceledOrder();

        assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
    }
}
