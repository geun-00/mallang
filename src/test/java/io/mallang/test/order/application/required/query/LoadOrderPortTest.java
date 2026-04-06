package io.mallang.test.order.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.OrderFixture.generateOrder;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PortTest
@DisplayName("LoadOrder Port")
class LoadOrderPortTest {

    @Test
    void getById로_Order를_조회한다(
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired LoadOrderPort loadOrderPort
    ) {
        // given
        Order order = generateOrder();
        saveOrderPort.save(order);

        // when & then
        assertThatCode(() -> loadOrderPort.getById(order.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void getById는_존재하지_않는_ID로_조회하면_OrderNotFoundException이_발생한다(
            @Autowired LoadOrderPort loadOrderPort
    ) {
        // given
        OrderId unknownId = new OrderId("unknown");

        // when & then
        assertThatThrownBy(() -> loadOrderPort.getById(unknownId))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
