package io.mallang.test.order.application.required.command;

import io.mallang.assertions.OrderAssertions;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.mallang.fixtures.OrderFixture.generateOrder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
}
