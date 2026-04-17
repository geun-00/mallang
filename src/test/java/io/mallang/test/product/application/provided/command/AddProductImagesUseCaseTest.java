package io.mallang.test.product.application.provided.command;

import io.mallang.annotations.UseCaseTest;
import io.mallang.product.application.provided.command.AddProductImagesUseCase;
import io.mallang.product.application.provided.command.model.AddProductImagesCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.NotProductSellerException;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.ProductFixture.generateSellerId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UseCaseTest
@DisplayName("AddProductImages UseCase")
class AddProductImagesUseCaseTest {

    @Test
    void 이미지_추가_성공_시_변경사항이_저장된다(
            @Autowired AddProductImagesUseCase addProductImagesUseCase,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProduct();
        saveProductPort.save(product);

        AddProductImagesCommand command = new AddProductImagesCommand(
                product.getSellerId().value(),
                product.getId().value(),
                List.of("https://test.com/image1.jpg", "https://test.com/image2.jpg")
        );

        // when
        addProductImagesUseCase.addImages(command);

        // then
        Product loaded = loadProductPort.getByIdWithImages(product.getId());

        assertThat(loaded.getThumbnailImage()).isNotNull();
        assertThat(loaded.getImages()).hasSize(command.imageUrls().size() - 1);
        assertThat(loaded.getThumbnailImage().imageUrl().value()).isEqualTo(command.imageUrls().getFirst());
        assertThat(loaded.getImages())
                .extracting(image -> image.imageUrl().value())
                .containsExactly(command.imageUrls().get(1));
    }

    @Test
    void 판매자가_아니면_예외가_발생한다(
            @Autowired AddProductImagesUseCase addProductImagesUseCase,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        Product product = generateProduct();
        saveProductPort.save(product);

        AddProductImagesCommand command = new AddProductImagesCommand(
                generateSellerId().value(),
                product.getId().value(),
                List.of("https://test.com/image1.jpg")
        );

        // when & then
        assertThatThrownBy(() -> addProductImagesUseCase.addImages(command))
                .isInstanceOf(NotProductSellerException.class);
    }

    @Test
    void 존재하지_않는_상품이면_예외가_발생한다(
            @Autowired AddProductImagesUseCase addProductImagesUseCase
    ) {
        // given
        AddProductImagesCommand command = new AddProductImagesCommand(
                generateSellerId().value(),
                "unknown-id",
                List.of("https://test.com/image1.jpg")
        );

        // when & then
        assertThatThrownBy(() -> addProductImagesUseCase.addImages(command))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
