package io.mallang.test.order.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.exception.MemberNotFoundException;
import io.mallang.member.domain.exception.NotOrderableMemberException;
import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.application.provided.command.model.CreateOrderResult;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.mallang.assertions.OrderAssertions.isCreatedFrom;
import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.MemberFixture.generateWithdrawnMember;
import static io.mallang.fixtures.OrderFixture.generateCreateOrderCommand;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.*;

@UseCaseTest
@DisplayName("CreateOrder UseCase")
class CreateOrderUseCaseTest {

    @Test
    void 주문을_생성하면_반환된_OrderId로_저장된_주문을_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadOrderPort loadOrderPort,
            @Autowired CreateOrderUseCase createOrderUseCase
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        Product product = generateProduct(5);
        saveProductPort.save(product);

        CreateOrderCommand command = generateCreateOrderCommand(
                member.getId(),
                List.of(new CreateOrderItemCommand(product.getId().value(), 2))
        );

        // when
        CreateOrderResult result = createOrderUseCase.place(command);

        // then
        assertThatCode(() -> loadOrderPort.getById(new OrderId(result.orderId())))
                .doesNotThrowAnyException();
    }

    @Test
    void 주문을_생성하면_주문_정보가_저장된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadOrderPort loadOrderPort,
            @Autowired CreateOrderUseCase createOrderUseCase
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        Product firstProduct = generateProduct(5);
        Product secondProduct = generateProduct(7);
        saveProductPort.save(firstProduct);
        saveProductPort.save(secondProduct);

        CreateOrderCommand command = generateCreateOrderCommand(
                member.getId(),
                List.of(
                        new CreateOrderItemCommand(firstProduct.getId().value(), 2),
                        new CreateOrderItemCommand(secondProduct.getId().value(), 3)
                )
        );

        // when
        CreateOrderResult result = createOrderUseCase.place(command);

        // then
        Order saved = loadOrderPort.getById(new OrderId(result.orderId()));
        assertThat(saved).satisfies(isCreatedFrom(command, List.of(firstProduct, secondProduct)));
    }

    @Test
    void 주문을_생성하면_주문한_수량만큼_재고가_차감된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort,
            @Autowired CreateOrderUseCase createOrderUseCase
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        Product product = generateProduct(5);
        saveProductPort.save(product);

        CreateOrderCommand command = generateCreateOrderCommand(
                member.getId(),
                List.of(new CreateOrderItemCommand(product.getId().value(), 2))
        );
        int expected = product.getStockQuantity().value() - command.items()
                                                                   .stream()
                                                                   .mapToInt(CreateOrderItemCommand::quantity)
                                                                   .sum();
        // when
        createOrderUseCase.place(command);

        // then
        Product saved = loadProductPort.getById(product.getId());
        assertThat(saved.getStockQuantity().value()).isEqualTo(expected);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired CreateOrderUseCase createOrderUseCase
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        CreateOrderCommand command = generateCreateOrderCommand(
                member.getId(),
                List.of(new CreateOrderItemCommand("unknown-product-id", 2))
        );

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.place(command))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 존재하지_않는_회원이면_예외가_발생한다(
            @Autowired CreateOrderUseCase createOrderUseCase
    ) {
        // given
        CreateOrderCommand command = generateCreateOrderCommand(new MemberId("unknown-id"));

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.place(command))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 주문할_수_없는_회원이면_예외가_발생한다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired CreateOrderUseCase createOrderUseCase
    ) {
        // given
        Member withdrawnMember = generateWithdrawnMember();
        saveMemberPort.save(withdrawnMember);

        CreateOrderCommand command = generateCreateOrderCommand(withdrawnMember.getId());

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.place(command))
                .isInstanceOf(NotOrderableMemberException.class);
    }
}
