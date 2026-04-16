package io.mallang.test.order.application.provided.query;

import io.mallang.annotations.UseCaseTest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.MemberId;
import io.mallang.order.application.provided.query.GetOrderDetailUseCase;
import io.mallang.order.application.provided.query.model.OrderDetailView;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.exception.NotOrdererException;
import io.mallang.order.domain.exception.OrderNotFoundException;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.MemberFixture.savedMemberId;
import static io.mallang.fixtures.OrderFixture.generateOrder;
import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("GetOrderDetail UseCase")
class GetOrderDetailUseCaseTest {

    @Test
    void 내_주문_상세를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired GetOrderDetailUseCase getOrderDetailUseCase
    ) {
        MemberId memberId = savedMemberId(saveMemberPort);

        Product product = generateProductWithImages();
        saveProductPort.save(product);

        int quantity = 2;
        Order order = generateOrder(memberId, product, quantity);
        saveOrderPort.save(order);

        OrderDetailView result = getOrderDetailUseCase.get(order.getId().value(), memberId.value());

        assertThat(result.orderId()).isEqualTo(order.getId().value());
        assertThat(result.memberId()).isEqualTo(memberId.value());
        assertThat(result.items()).hasSize(order.getItems().size());
        assertThat(result.items().getFirst().productId()).isEqualTo(product.getId().value());
    }

    @Test
    void 내_주문이_아니면_NotOrdererException이_발생한다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveOrderPort saveOrderPort,
            @Autowired GetOrderDetailUseCase getOrderDetailUseCase
    ) {
        MemberId ordererId = savedMemberId(saveMemberPort);
        MemberId requesterId = savedMemberId(saveMemberPort);

        Product product = generateProductWithImages();
        saveProductPort.save(product);

        Order order = generateOrder(ordererId, product, 1);
        saveOrderPort.save(order);

        String orderIdValue = order.getId().value();
        String requesterIdValue = requesterId.value();
        assertThatThrownBy(() -> getOrderDetailUseCase.get(orderIdValue, requesterIdValue))
                .isInstanceOf(NotOrdererException.class);
    }

    @Test
    void 존재하지_않는_주문이면_OrderNotFoundException이_발생한다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired GetOrderDetailUseCase getOrderDetailUseCase
    ) {
        MemberId memberId = savedMemberId(saveMemberPort);

        String memberIdValue = memberId.value();
        assertThatThrownBy(() -> getOrderDetailUseCase.get("unknown", memberIdValue))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
