package io.mallang.test.product.application.provided.command;

import io.mallang.TestConfig;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.UpdateProductUseCase;
import io.mallang.product.application.provided.command.model.UpdateProductCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.NotProductSellerException;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.mallang.ProductAssertions.isDerivedFrom;
import static io.mallang.fixtures.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
class UpdateProductUseCaseTest {

    @Test
    void 상품_수정_성공_시_변경사항이_저장된다(
            @Autowired UpdateProductUseCase updateProductUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        MemberId sellerId = generateSellerId();
        Product before = generateProduct(sellerId, 5);
        saveProductPort.save(before);

        UpdateProductCommand command = new UpdateProductCommand(
                sellerId.value(),
                before.getId().value(),
                "수정된 상품명",
                "수정된 설명",
                before.getPrice().value(),
                "BOOKS"
        );

        // when
        updateProductUseCase.update(command);

        // then
        Product after = loadProductPort.getById(before.getId());
        assertThat(after).satisfies(isDerivedFrom(command));
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired UpdateProductUseCase updateProductUseCase,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Product product = generateProduct(5);
        saveProductPort.save(product);

        UpdateProductCommand command = new UpdateProductCommand(
                generateSellerId().value(),
                product.getId().value(),
                "수정된 상품명",
                "수정된 설명",
                product.getPrice().value(),
                "BOOKS"
        );

        // when & then
        assertThatThrownBy(() -> updateProductUseCase.update(command))
                .isInstanceOf(NotProductSellerException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired UpdateProductUseCase updateProductUseCase
    ) {
        // given
        UpdateProductCommand command = new UpdateProductCommand(
                generateSellerId().value(),
                "unknown-id",
                "수정된 상품명",
                "수정된 설명",
                java.math.BigDecimal.valueOf(10000),
                "BOOKS"
        );

        // when & then
        assertThatThrownBy(() -> updateProductUseCase.update(command))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
