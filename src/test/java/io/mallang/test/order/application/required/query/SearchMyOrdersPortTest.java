package io.mallang.test.order.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.common.application.query.SliceResult;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.MemberId;
import io.mallang.order.application.provided.query.model.OrderListView;
import io.mallang.order.application.provided.query.model.SearchMyOrdersQuery;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.SearchMyOrdersPort;
import io.mallang.order.domain.Order;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.MemberFixture.savedMemberId;
import static io.mallang.fixtures.OrderFixture.generateCanceledOrder;
import static io.mallang.fixtures.OrderFixture.generateOrder;
import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static io.mallang.assertions.OrderAssertions.isSummaryOf;
import static org.assertj.core.api.Assertions.assertThat;

@PortTest
@DisplayName("SearchMyOrders Port")
class SearchMyOrdersPortTest {

    @Test
    void 회원의_주문_목록을_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired SearchMyOrdersPort searchMyOrdersPort
    ) {
        MemberId memberId = savedMemberId(saveMemberPort);

        Product product = generateProductWithImages();
        saveProductPort.save(product);

        int quantity = 2;
        Order order = generateOrder(memberId, product, quantity);
        saveOrderPort.save(order);

        SliceResult<OrderListView> result = searchMyOrdersPort.search(new SearchMyOrdersQuery(
                memberId.value(), null, null, 20
        ));

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst()).satisfies(isSummaryOf(order, product, quantity));
    }

    @Test
    void 상태로_주문_목록을_필터링할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired SearchMyOrdersPort searchMyOrdersPort
    ) {
        MemberId memberId = savedMemberId(saveMemberPort);

        Product product = generateProductWithImages();
        saveProductPort.save(product);

        Order waitingOrder = generateOrder(memberId, product, 1);
        Order canceledOrder = generateCanceledOrder(memberId, product, 1);
        saveOrderPort.save(waitingOrder);
        saveOrderPort.save(canceledOrder);

        SliceResult<OrderListView> result = searchMyOrdersPort.search(new SearchMyOrdersQuery(
                memberId.value(), "CANCELED", null, 20
        ));

        assertThat(result.items()).extracting(OrderListView::orderId)
                                  .containsExactly(canceledOrder.getId().value())
                                  .doesNotContain(waitingOrder.getId().value());
    }

    @Test
    void lastOrderId_기준으로_다음_슬라이스를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired SearchMyOrdersPort searchMyOrdersPort
    ) {
        MemberId memberId = savedMemberId(saveMemberPort);

        Product product = generateProductWithImages();
        saveProductPort.save(product);

        saveOrderPort.save(generateOrder(memberId, product, 1));
        saveOrderPort.save(generateOrder(memberId, product, 1));
        saveOrderPort.save(generateOrder(memberId, product, 1));

        SliceResult<OrderListView> firstSlice = searchMyOrdersPort.search(new SearchMyOrdersQuery(
                memberId.value(), null, null, 2
        ));
        SliceResult<OrderListView> secondSlice = searchMyOrdersPort.search(new SearchMyOrdersQuery(
                memberId.value(), null, firstSlice.nextCursor(), 2
        ));

        assertThat(firstSlice.items()).hasSize(2);
        assertThat(firstSlice.hasNext()).isTrue();
        assertThat(firstSlice.nextCursor()).isEqualTo(firstSlice.items().getLast().orderId());

        assertThat(secondSlice.items()).hasSize(1);
        assertThat(secondSlice.hasNext()).isFalse();
        assertThat(secondSlice.nextCursor()).isNull();
        assertThat(secondSlice.items()).extracting(OrderListView::orderId)
                                       .doesNotContainAnyElementsOf(firstSlice.items()
                                                                              .stream()
                                                                              .map(OrderListView::orderId)
                                                                              .toList());
    }
}
