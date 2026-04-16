package io.mallang.test.order.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.MemberId;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderDetailPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.command.PlaceOrderItemCommand;
import io.mallang.order.domain.exception.OrderNotFoundException;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static io.mallang.assertions.OrderAssertions.isDetailOf;
import static io.mallang.fixtures.MemberFixture.savedMemberId;
import static io.mallang.fixtures.OrderFixture.generateOrderWithItems;
import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static io.mallang.order.application.provided.query.model.OrderDetailView.OrderItemView;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PortTest
@DisplayName("LoadOrderDetail Port")
class LoadOrderDetailPortTest {

    @Test
    void 주문_상세를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired LoadOrderDetailPort loadOrderDetailPort
    ) {
        MemberId memberId = savedMemberId(saveMemberPort);

        Product product = generateProductWithImages();
        saveProductPort.save(product);

        int quantity = 2;
        List<PlaceOrderItemCommand> items = List.of(new PlaceOrderItemCommand(product.getId(), quantity, product.getPrice()));
        Order order = generateOrderWithItems(memberId, items);
        saveOrderPort.save(order);

        OrderDetailView result = loadOrderDetailPort.load(order.getId().value());

        assertThat(result).isNotNull()
                          .satisfies(isDetailOf(order, product, quantity));
    }

    @Test
    void 여러_주문_상품을_포함한_주문_상세를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired LoadOrderDetailPort loadOrderDetailPort
    ) {
        MemberId memberId = savedMemberId(saveMemberPort);

        Product firstProduct = generateProductWithImages();
        Product secondProduct = generateProductWithImages();
        saveProductPort.save(firstProduct);
        saveProductPort.save(secondProduct);

        int firstQuantity = 2;
        int secondQuantity = 3;
        List<PlaceOrderItemCommand> items = List.of(
                new PlaceOrderItemCommand(firstProduct.getId(), firstQuantity, firstProduct.getPrice()),
                new PlaceOrderItemCommand(secondProduct.getId(), secondQuantity, secondProduct.getPrice())
        );
        Order order = generateOrderWithItems(memberId, items);
        saveOrderPort.save(order);

        OrderDetailView result = loadOrderDetailPort.load(order.getId().value());

        BigDecimal expectedTotalPrice = firstProduct.getPrice()
                                                    .multiply(firstQuantity)
                                                    .value()
                                                    .add(secondProduct.getPrice()
                                                                      .multiply(secondQuantity)
                                                                      .value());
        assertThat(result.items()).hasSize(2);
        assertThat(result.items()).extracting(OrderItemView::productId)
                                  .containsExactlyInAnyOrder(
                                          firstProduct.getId().value(),
                                          secondProduct.getId().value()
                                  );
        assertThat(result.totalPrice()).isEqualByComparingTo(expectedTotalPrice);
    }

    @Test
    void 존재하지_않는_주문이면_OrderNotFoundException이_발생한다(
            @Autowired LoadOrderDetailPort loadOrderDetailPort
    ) {
        assertThatThrownBy(() -> loadOrderDetailPort.load("unknown"))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
