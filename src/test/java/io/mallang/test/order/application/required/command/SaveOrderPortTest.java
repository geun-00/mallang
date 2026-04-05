package io.mallang.test.order.application.required.command;

import io.mallang.PortTest;
import io.mallang.assertions.OrderAssertions;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.OrderFixture.generateOrder;
import static org.assertj.core.api.Assertions.assertThat;

@PortTest
@DisplayName("SaveOrder Port")
class SaveOrderPortTest {

    @Test
    void 저장하면_조회된다(
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired LoadOrderPort loadOrderPort
    ) {
        // given
        Order order = generateOrder();

        // when
        saveOrderPort.save(order);

        // then
        assertThat(loadOrderPort.getById(order.getId()))
                .isNotNull()
                .satisfies(OrderAssertions.isSameAs(order));
    }

    @Test
    void 저장한_주문을_취소한_뒤_다시_저장하면_변경사항이_반영된다(
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired LoadOrderPort loadOrderPort
    ) {
        // given
        Order order = generateOrder();
        saveOrderPort.save(order);
        order.cancel();

        // when
        saveOrderPort.save(order);

        // then
        assertThat(loadOrderPort.getById(order.getId()))
                .isNotNull()
                .satisfies(OrderAssertions.isSameAs(order));
    }
}
