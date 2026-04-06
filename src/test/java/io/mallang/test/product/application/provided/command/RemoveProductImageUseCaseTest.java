package io.mallang.test.product.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.product.application.provided.command.RemoveProductImageUseCase;
import io.mallang.product.application.provided.command.model.RemoveProductImageCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductImageId;
import io.mallang.product.domain.exception.NotProductSellerException;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static io.mallang.fixtures.ProductFixture.generateSellerId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("RemoveProductImage UseCase")
class RemoveProductImageUseCaseTest {

    @Test
    void 이미지_삭제_성공_시_변경사항이_저장된다(
            @Autowired RemoveProductImageUseCase removeProductImageUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product before = generateProductWithImages();
        saveProductPort.save(before);

        Product loaded = loadProductPort.getByIdWithImages(before.getId());
        ProductImageId targetImageId = loaded.getThumbnailImage().id();

        RemoveProductImageCommand command = new RemoveProductImageCommand(
                loaded.getSellerId().value(),
                loaded.getId().value(),
                targetImageId.value()
        );

        // when
        removeProductImageUseCase.removeImage(command);

        // then
        Product after = loadProductPort.getByIdWithImages(before.getId());
        assertThat(after.getThumbnailImage()).isNotNull();
        assertThat(after.getThumbnailImage().id()).isNotEqualTo(targetImageId);
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired RemoveProductImageUseCase removeProductImageUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProductWithImages();
        saveProductPort.save(product);

        Product loaded = loadProductPort.getByIdWithImages(product.getId());
        RemoveProductImageCommand command = new RemoveProductImageCommand(
                generateSellerId().value(),
                loaded.getId().value(),
                loaded.getThumbnailImage().id().value()
        );

        // when & then
        assertThatThrownBy(() -> removeProductImageUseCase.removeImage(command))
                .isInstanceOf(NotProductSellerException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired RemoveProductImageUseCase removeProductImageUseCase
    ) {
        // given
        RemoveProductImageCommand command = new RemoveProductImageCommand(
                generateSellerId().value(),
                "unknown-id",
                "unknown-image-id"
        );

        // when & then
        assertThatThrownBy(() -> removeProductImageUseCase.removeImage(command))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
