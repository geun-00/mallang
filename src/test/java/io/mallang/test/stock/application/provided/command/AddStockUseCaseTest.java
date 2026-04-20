package io.mallang.test.stock.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.NotProductSellerException;
import io.mallang.product.domain.exception.ProductNotFoundException;
import io.mallang.stock.application.provided.command.AddStockUseCase;
import io.mallang.stock.application.provided.command.model.AddStockCommand;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.application.required.query.LoadStockPort;
import io.mallang.stock.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.mallang.fixtures.ProductFixture.generateDiscontinuedProduct;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.ProductFixture.generateSellerId;
import static io.mallang.fixtures.StockFixture.generateStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("AddStockUseCase UseCase")
class AddStockUseCaseTest {

    @Test
    void 재고_추가_성공_시_변경사항이_저장된다(
            @Autowired AddStockUseCase addStockUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadStockPort loadStockPort
    ) {
        // given
        Product product = generateProduct();
        saveProductPort.save(product);

        Stock beforeStock = generateStock(product, 5);
        saveStockPort.save(beforeStock);

        AddStockCommand command = new AddStockCommand(
                product.getSellerId().value(),
                product.getId().value(),
                3
        );

        // when
        addStockUseCase.addStock(command);

        // then
        int expected = beforeStock.getQuantity().value() + command.quantity();

        Stock after = loadStockPort.getByProductId(product.getId());
        assertThat(after.getQuantity().value()).isEqualTo(expected);
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired AddStockUseCase addStockUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveStockPort saveStockPort
    ) {
        // given
        Product product = generateProduct();
        saveProductPort.save(product);
        saveStockPort.save(generateStock(product, 5));

        AddStockCommand command = new AddStockCommand(
                generateSellerId().value(),
                product.getId().value(),
                3
        );

        // when & then
        assertThatThrownBy(() -> addStockUseCase.addStock(command))
                .isInstanceOf(NotProductSellerException.class);
    }

    @Test
    void 주문할_수_없는_상품이면_예외가_발생한다(
            @Autowired AddStockUseCase addStockUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveStockPort saveStockPort
    ) {
        // given
        Product product = generateDiscontinuedProduct();
        saveProductPort.save(product);

        saveStockPort.save(generateStock(product, 5));
        AddStockCommand command = new AddStockCommand(
                product.getSellerId().value(),
                product.getId().value(),
                3
        );

        // when & then
        assertThatThrownBy(() -> addStockUseCase.addStock(command))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired AddStockUseCase addStockUseCase
    ) {
        // given
        AddStockCommand command = new AddStockCommand(
                generateSellerId().value(),
                "unknown-id",
                3
        );

        // when & then
        assertThatThrownBy(() -> addStockUseCase.addStock(command))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Nested
    class 동시성 {

        @Test
        void 동시에_재고를_추가해도_최종_수량이_정확하다(
                @Autowired AddStockUseCase addStockUseCase,
                @Autowired SaveProductPort saveProductPort,
                @Autowired SaveStockPort saveStockPort,
                @Autowired LoadStockPort loadStockPort
        ) throws InterruptedException {
            // given
            int initialQuantity = 50;
            int requestCount = 30;

            Product product = generateProduct();
            saveProductPort.save(product);
            saveStockPort.save(generateStock(product, initialQuantity));

            AddStockCommand command = new AddStockCommand(
                    product.getSellerId().value(),
                    product.getId().value(),
                    1
            );

            ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(requestCount);
            Queue<Throwable> failures = new ConcurrentLinkedQueue<>();

            try {
                for (int i = 0; i < requestCount; i++) {
                    executorService.execute(() -> {
                        try {
                            startLatch.await();
                            addStockUseCase.addStock(command);
                        } catch (Throwable throwable) {
                            failures.add(throwable);
                        } finally {
                            doneLatch.countDown();
                        }
                    });
                }

                // when
                startLatch.countDown();

                // then
                assertThat(doneLatch.await(10, TimeUnit.SECONDS)).isTrue();
                assertThat(failures).isEmpty();

                Stock after = loadStockPort.getByProductId(product.getId());
                assertThat(after.getQuantity().value()).isEqualTo(initialQuantity + requestCount);
            } finally {
                executorService.shutdownNow();
                executorService.close();
            }
        }
    }
}
