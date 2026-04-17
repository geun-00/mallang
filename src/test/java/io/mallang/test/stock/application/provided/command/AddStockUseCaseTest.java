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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        saveStockPort.save(generateStock(product.getId(), 5));

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

        saveStockPort.save(generateStock(product.getId(), 5));
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
}
