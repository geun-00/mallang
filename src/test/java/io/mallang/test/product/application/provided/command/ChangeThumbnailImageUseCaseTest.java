package io.mallang.test.product.application.provided.command;

import io.mallang.TestConfig;
import io.mallang.product.application.provided.command.ChangeThumbnailImageUseCase;
import io.mallang.product.application.provided.command.model.ChangeThumbnailImageCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductImage;
import io.mallang.product.domain.exception.NotProductSellerException;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static io.mallang.fixtures.ProductFixture.generateSellerId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestConfig.class)
class ChangeThumbnailImageUseCaseTest {

    @Test
    void 대표이미지_변경_성공_시_변경사항이_저장된다(
            @Autowired ChangeThumbnailImageUseCase changeThumbnailImageUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product before = generateProductWithImages();
        saveProductPort.save(before);

        Product loaded = loadProductPort.getByIdWithImages(before.getId());
        ProductImage targetImage = loaded.getImages().getFirst();

        ChangeThumbnailImageCommand command = new ChangeThumbnailImageCommand(
                loaded.getSellerId().value(),
                loaded.getId().value(),
                targetImage.id().value()
        );

        // when
        changeThumbnailImageUseCase.changeThumbnail(command);

        // then
        Product after = loadProductPort.getByIdWithImages(before.getId());
        assertThat(after.getThumbnailImage()).isEqualTo(targetImage);
        assertThat(after.getImages()).doesNotContain(targetImage);
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired ChangeThumbnailImageUseCase changeThumbnailImageUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProductWithImages();
        saveProductPort.save(product);

        Product loaded = loadProductPort.getByIdWithImages(product.getId());
        ChangeThumbnailImageCommand command = new ChangeThumbnailImageCommand(
                generateSellerId().value(),
                loaded.getId().value(),
                loaded.getImages().getFirst().id().value()
        );

        // when & then
        assertThatThrownBy(() -> changeThumbnailImageUseCase.changeThumbnail(command))
                .isInstanceOf(NotProductSellerException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired ChangeThumbnailImageUseCase changeThumbnailImageUseCase
    ) {
        // given
        ChangeThumbnailImageCommand command = new ChangeThumbnailImageCommand(
                generateSellerId().value(),
                "unknown-id",
                "unknown-image-id"
        );

        // when & then
        assertThatThrownBy(() -> changeThumbnailImageUseCase.changeThumbnail(command))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
