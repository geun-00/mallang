package io.mallang.test.order.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.common.domain.exception.InvalidValueException;
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
import io.mallang.product.domain.Product;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.application.required.query.LoadStockPort;
import io.mallang.stock.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.OrderFixture.generateCanceledOrder;
import static io.mallang.fixtures.OrderFixture.generateCreateOrderCommand;
import static io.mallang.fixtures.OrderFixture.generateOrder;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.StockFixture.generateStock;
import static io.mallang.test.support.concurrency.ConcurrentTestExecutor.executeConcurrently;
import static io.mallang.test.support.concurrency.ConcurrentTestExecutor.executeConcurrentlyAndCollectFailures;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("CancelOrder UseCase")
class CancelOrderUseCaseTest {

    @Test
    void 주문_취소_성공_시_변경사항이_저장된다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveStockPort saveStockPort,
            @Autowired CreateOrderUseCase createOrderUseCase,
            @Autowired CancelOrderUseCase cancelOrderUseCase,
            @Autowired LoadOrderPort loadOrderPort
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        Product product = generateProduct();
        saveProductPort.save(product);
        saveStockPort.save(generateStock(product, 5));

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
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadStockPort loadStockPort,
            @Autowired CreateOrderUseCase createOrderUseCase,
            @Autowired CancelOrderUseCase cancelOrderUseCase
    ) {
        // given
        Member member = generateMember();
        saveMemberPort.save(member);

        Product product = generateProduct();
        saveProductPort.save(product);
        saveStockPort.save(generateStock(product, 5));

        CreateOrderCommand createCommand = generateCreateOrderCommand(
                member.getId(),
                List.of(new CreateOrderItemCommand(product.getId().value(), 2))
        );

        CreateOrderResult result = createOrderUseCase.place(createCommand);
        CancelOrderCommand cancelCommand = new CancelOrderCommand(result.orderId(), member.getId().value());

        // when
        cancelOrderUseCase.cancel(cancelCommand);

        // then
        Stock restored = loadStockPort.getByProductId(product.getId());
        assertThat(restored.getQuantity().value()).isEqualTo(5);
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
            @Autowired SaveStockPort saveStockPort,
            @Autowired CreateOrderUseCase createOrderUseCase,
            @Autowired CancelOrderUseCase cancelOrderUseCase
    ) {
        // given
        Member orderer = generateMember();
        Member otherMember = generateMember();
        saveMemberPort.save(orderer);
        saveMemberPort.save(otherMember);

        Product product = generateProduct();
        saveProductPort.save(product);
        saveStockPort.save(generateStock(product, 5));

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

    @Nested
    class 동시성 {

        @Test
        void 동시에_주문을_취소해도_최종_재고_수량이_정확하다(
                @Autowired SaveMemberPort saveMemberPort,
                @Autowired SaveProductPort saveProductPort,
                @Autowired SaveStockPort saveStockPort,
                @Autowired LoadStockPort loadStockPort,
                @Autowired CreateOrderUseCase createOrderUseCase,
                @Autowired CancelOrderUseCase cancelOrderUseCase
        ) throws InterruptedException {
            // given
            int initialQuantity = 50;
            int requestCount = 30;

            Member member = generateMember();
            saveMemberPort.save(member);

            Product product = generateProduct();
            saveProductPort.save(product);
            saveStockPort.save(generateStock(product, initialQuantity));

            CreateOrderCommand createCommand = generateCreateOrderCommand(
                    member.getId(),
                    List.of(new CreateOrderItemCommand(product.getId().value(), 1))
            );

            List<CancelOrderCommand> cancelCommands = new ArrayList<>();
            for (int i = 0; i < requestCount; i++) {
                CreateOrderResult result = createOrderUseCase.place(createCommand);
                cancelCommands.add(new CancelOrderCommand(result.orderId(), member.getId().value()));
            }

            // when
            executeConcurrently(cancelCommands, cancelOrderUseCase::cancel);

            // then
            Stock after = loadStockPort.getByProductId(product.getId());
            assertThat(after.getQuantity().value()).isEqualTo(initialQuantity);
        }

        @Test
        void 동일한_주문을_동시에_취소해도_재고는_한_번만_복구된다(
                @Autowired SaveMemberPort saveMemberPort,
                @Autowired SaveProductPort saveProductPort,
                @Autowired SaveStockPort saveStockPort,
                @Autowired LoadStockPort loadStockPort,
                @Autowired CreateOrderUseCase createOrderUseCase,
                @Autowired CancelOrderUseCase cancelOrderUseCase
        ) throws InterruptedException {
            // given
            int initialQuantity = 50;
            int orderQuantity = 2;
            int requestCount = 10;

            Member member = generateMember();
            saveMemberPort.save(member);

            Product product = generateProduct();
            saveProductPort.save(product);
            saveStockPort.save(generateStock(product, initialQuantity));

            CreateOrderCommand createCommand = generateCreateOrderCommand(
                    member.getId(),
                    List.of(new CreateOrderItemCommand(product.getId().value(), orderQuantity))
            );
            CreateOrderResult result = createOrderUseCase.place(createCommand);
            CancelOrderCommand cancelCommand = new CancelOrderCommand(result.orderId(), member.getId().value());

            // when
            Queue<Throwable> failures = executeConcurrentlyAndCollectFailures(
                    requestCount,
                    () -> cancelOrderUseCase.cancel(cancelCommand)
            );

            // then
            assertThat(failures).hasSize(requestCount - 1)
                                .allSatisfy(failure -> assertThat(failure).isInstanceOf(InvalidValueException.class));

            Stock after = loadStockPort.getByProductId(product.getId());
            assertThat(after.getQuantity().value()).isEqualTo(initialQuantity);
        }
    }
}
