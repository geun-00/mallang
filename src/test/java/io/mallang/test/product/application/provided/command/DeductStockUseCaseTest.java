package io.mallang.test.product.application.provided.command;

import io.mallang.UseCaseTest;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.DeductStockUseCase;
import io.mallang.product.application.provided.command.model.DeductStockCommand;
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
@DisplayName("DeductStockUseCase UseCase")
class DeductStockUseCaseTest {

    @Test
    void 재고_차감_성공_시_변경사항이_저장된다(
            @Autowired DeductStockUseCase deductStockUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        MemberId sellerId = generateSellerId();
        Product before = generateProduct(sellerId, 5);
        saveProductPort.save(before);
        DeductStockCommand command = new DeductStockCommand(
                sellerId.value(),
                before.getId().value(),
                3
        );

        // when
        deductStockUseCase.deductStock(command);

        // then
        int expected = before.getStockQuantity().value() - command.quantity();

        Product after = loadProductPort.getById(before.getId());
        assertThat(after.getStockQuantity().value()).isEqualTo(expected);
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired DeductStockUseCase deductStockUseCase,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Product product = generateProduct(5);
        saveProductPort.save(product);
        DeductStockCommand command = new DeductStockCommand(
                generateSellerId().value(),
                product.getId().value(),
                3
        );

        // when & then
        assertThatThrownBy(() -> deductStockUseCase.deductStock(command))
                .isInstanceOf(NotProductSellerException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired DeductStockUseCase deductStockUseCase
    ) {
        // given
        DeductStockCommand command = new DeductStockCommand(
                generateSellerId().value(),
                "unknown-id",
                3
        );

        // when & then
        assertThatThrownBy(() -> deductStockUseCase.deductStock(command))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
