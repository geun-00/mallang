package io.mallang.test.product.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.AddStockUseCase;
import io.mallang.product.application.provided.command.model.AddStockCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.NotProductSellerException;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.ProductFixture.generateSellerId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("AddStockUseCase UseCase")
class AddStockUseCaseTest {

    @Test
    void 재고_추가_성공_시_변경사항이_저장된다(
            @Autowired AddStockUseCase addStockUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        MemberId sellerId = generateSellerId();
        Product before = generateProduct(sellerId, 5);
        saveProductPort.save(before);
        AddStockCommand command = new AddStockCommand(
                sellerId.value(),
                before.getId().value(),
                3
        );

        // when
        addStockUseCase.addStock(command);

        // then
        int expected = before.getStockQuantity().value() + command.quantity();

        Product after = loadProductPort.getById(before.getId());
        assertThat(after.getStockQuantity().value()).isEqualTo(expected);
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired AddStockUseCase addStockUseCase,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Product product = generateProduct(5);
        saveProductPort.save(product);
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