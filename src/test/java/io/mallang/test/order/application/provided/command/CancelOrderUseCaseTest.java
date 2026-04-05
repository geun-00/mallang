package io.mallang.test.order.application.provided.command;

import io.mallang.UseCaseTest;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.order.application.provided.command.CancelOrderUseCase;
import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.order.application.provided.command.model.CancelOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.application.provided.command.model.CreateOrderResult;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.OrderStatus;
import io.mallang.order.domain.exception.NotOrdererException;
import io.mallang.order.domain.exception.OrderNotFoundException;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.OrderFixture.*;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("CancelOrder UseCase")
class CancelOrderUseCaseTest {

    @Test
    void 주문_취소_성공_시_변경사항이_저장된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired CreateOrderUseCase createOrderUseCase,
            @Autowired CancelOrderUseCase cancelOrderUseCase,
            @Autowired LoadOrderPort loadOrderPort
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        Product product = generateProduct(5);
        saveProductPort.save(product);

        CreateOrderCommand createCommand = generateCreateOrderCommand(
                member.getId(),
                List.of(new CreateOrderItemCommand(product.getId().value(), 2))
        );
        CreateOrderResult result = createOrderUseCase.place(createCommand);
        CancelOrderCommand command = new CancelOrderCommand(result.orderId(), member.getId().value());

        // when
        cancelOrderUseCase.cancel(command);

        // then
        Order after = loadOrderPort.getById(new OrderId(result.orderId()));
        assertThat(after.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void 주문_취소_성공_시_차감된_재고가_복구된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort,
            @Autowired CreateOrderUseCase createOrderUseCase,
            @Autowired CancelOrderUseCase cancelOrderUseCase
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        Product product = generateProduct(5);
        saveProductPort.save(product);

        CreateOrderCommand createCommand = generateCreateOrderCommand(
                member.getId(),
                List.of(new CreateOrderItemCommand(product.getId().value(), 2))
        );

        CreateOrderResult result = createOrderUseCase.place(createCommand);
        CancelOrderCommand cancelCommand = new CancelOrderCommand(result.orderId(), member.getId().value());

        // when
        cancelOrderUseCase.cancel(cancelCommand);

        // then
        Product restored = loadProductPort.getById(product.getId());
        assertThat(restored.getStockQuantity().value()).isEqualTo(5);
    }

    @Test
    void 존재하지_않는_주문이면_예외가_발생한다(
            @Autowired CancelOrderUseCase cancelOrderUseCase
    ) {
        // given
        CancelOrderCommand command = new CancelOrderCommand("unknown-id", "member-id");

        // when & then
        assertThatThrownBy(() -> cancelOrderUseCase.cancel(command))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void 주문자가_아니면_예외가_발생한다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired CreateOrderUseCase createOrderUseCase,
            @Autowired CancelOrderUseCase cancelOrderUseCase
    ) {
        // given
        Member orderer = generateMember();
        Member otherMember = generateMember();
        saveMemberPort.save(orderer);
        saveMemberPort.save(otherMember);

        Product product = generateProduct(5);
        saveProductPort.save(product);

        CreateOrderCommand createCommand = generateCreateOrderCommand(
                orderer.getId(),
                List.of(new CreateOrderItemCommand(product.getId().value(), 2))
        );
        CreateOrderResult result = createOrderUseCase.place(createCommand);
        CancelOrderCommand command = new CancelOrderCommand(result.orderId(), otherMember.getId().value());

        // when & then
        assertThatThrownBy(() -> cancelOrderUseCase.cancel(command))
                .isInstanceOf(NotOrdererException.class);
    }

    @Test
    void 취소할_수_없는_주문이면_예외가_발생한다(
            @Autowired CancelOrderUseCase cancelOrderUseCase,
            @Autowired SaveOrderPort saveOrderPort
    ) {
        // given
        Order order = generateOrder();
        order.nextStatus();
        order.nextStatus();
        saveOrderPort.save(order);

        CancelOrderCommand command = new CancelOrderCommand(order.getId().value(), order.getMemberId().value());

        // when & then
        assertThatThrownBy(() -> cancelOrderUseCase.cancel(command))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 이미_취소된_주문이면_예외가_발생한다(
            @Autowired CancelOrderUseCase cancelOrderUseCase,
            @Autowired SaveOrderPort saveOrderPort
    ) {
        // given
        Order order = generateCanceledOrder();
        saveOrderPort.save(order);
        CancelOrderCommand command = new CancelOrderCommand(order.getId().value(), order.getMemberId().value());

        // when & then
        assertThatThrownBy(() -> cancelOrderUseCase.cancel(command))
                .isInstanceOf(InvalidValueException.class);
    }
}
