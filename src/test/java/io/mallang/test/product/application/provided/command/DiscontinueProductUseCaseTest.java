package io.mallang.test.product.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.DiscontinueProductUseCase;
import io.mallang.product.application.provided.command.model.DiscontinueProductCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductStatus;
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
@DisplayName("DiscontinueProduct UseCase")
class DiscontinueProductUseCaseTest {

    @Test
    void 판매_중단_성공_시_변경사항이_저장된다(
            @Autowired DiscontinueProductUseCase discontinueProductUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        MemberId sellerId = generateSellerId();
        Product before = generateProduct(sellerId, 5);
        saveProductPort.save(before);

        DiscontinueProductCommand command = new DiscontinueProductCommand(
                sellerId.value(),
                before.getId().value()
        );

        // when
        discontinueProductUseCase.discontinue(command);

        // then
        Product after = loadProductPort.getById(before.getId());
        assertThat(after.getStatus()).isEqualTo(ProductStatus.DISCONTINUED);
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired DiscontinueProductUseCase discontinueProductUseCase,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Product product = generateProduct(5);
        saveProductPort.save(product);

        DiscontinueProductCommand command = new DiscontinueProductCommand(
                generateSellerId().value(),
                product.getId().value()
        );

        // when & then
        assertThatThrownBy(() -> discontinueProductUseCase.discontinue(command))
                .isInstanceOf(NotProductSellerException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired DiscontinueProductUseCase discontinueProductUseCase
    ) {
        // given
        DiscontinueProductCommand command = new DiscontinueProductCommand(
                generateSellerId().value(),
                "unknown-id"
        );

        // when & then
        assertThatThrownBy(() -> discontinueProductUseCase.discontinue(command))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
