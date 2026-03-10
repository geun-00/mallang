package io.mallang.test.product.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.fixtures.ProductFixture;
import io.mallang.product.domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.mallang.ProductAssertions.isDerivedFrom;
import static io.mallang.fixtures.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void 유효한_정보로_상품을_생성하면_식별자가_할당된다() {
        Product product = generateProduct();

        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getId().value()).isNotNull();
    }

    @Test
    void 유효한_정보로_상품을_생성하면_상품명_설명_가격_재고_수량_카테고리가_저장된다() {
        ProductCreateCommand createCommand = generateProductCreateCommand();
        IdGenerator idGenerator = generateIdGenerator();

        Product product = Product.create(createCommand, idGenerator);

        assertThat(product).satisfies(isDerivedFrom(createCommand));
    }

    @Test
    void 재고가_1_이상이면_ON_SALE_상태로_생성된다() {
        Product product = generateProduct(1);

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    void 재고가_0이면_SOLD_OUT_상태로_생성된다() {
        Product product = generateProduct(0);

        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    void 재고를_추가하면_수량이_증가한다() {
        // given
        Product product = generateProduct();
        int oldStockQuantity = product.getStockQuantity().value();

        // when
        int additionalStock = 3;
        product.addStock(additionalStock);

        // then
        assertThat(product.getStockQuantity().value()).isEqualTo(oldStockQuantity + additionalStock);
    }

    @Test
    void 재고를_차감하면_수량이_감소한다() {
        // given
        Product product = generateProduct();
        int oldStockQuantity = product.getStockQuantity().value();

        // when
        int deductedStock = 3;
        product.deductStock(deductedStock);

        // then
        assertThat(product.getStockQuantity().value()).isEqualTo(oldStockQuantity - deductedStock);
    }

    @Test
    void 재고_차감_후_0이_되면_SOLD_OUT_상태가_된다() {
        // given
        Product product = generateProduct(3);

        // when
        product.deductStock(3);

        // then
        assertThat(product.getStockQuantity().value()).isEqualTo(0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    void 재고_추가_후_1_이상이_되면_ON_SALE_상태가_된다() {
        // given
        Product product = generateProduct(0);

        // when
        product.addStock(1);

        // then
        assertThat(product.getStockQuantity().value()).isEqualTo(1);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    void 재고_차감_시_보유_재고보다_많은_수량을_차감하면_예외가_발생한다() {
        // given
        Product product = generateProduct(2);

        // when
        // then
        assertThatThrownBy(() -> product.deductStock(3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_정보를_수정할_수_있다() {
        // given
        Product product = generateProduct();

        // when
        ModifyProductCommand modifyCommand = generateModifyProductCommand();
        product.modify(modifyCommand);

        // then
        assertThat(product).satisfies(isDerivedFrom(modifyCommand));
    }

    @Test
    void 판매_중단하면_DISCONTINUED_상태가_된다() {
        // given
        Product product = generateProduct();

        // when
        product.discontinue();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DISCONTINUED);
    }

    @Test
    void DISCONTINUED_상품은_재고를_추가할_수_없다() {
        // given
        Product product = generateDiscontinuedProduct();

        // when
        // then
        assertThatThrownBy(() -> product.addStock(1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void DISCONTINUED_상품은_재고를_차감할_수_없다() {
        // given
        Product product = generateDiscontinuedProduct();

        // when
        // then
        assertThatThrownBy(() -> product.deductStock(1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void DISCONTINUED_상품은_수정할_수_없다() {
        // given
        Product product = generateDiscontinuedProduct();

        // when
        // then
        assertThatThrownBy(() -> product.modify(generateModifyProductCommand()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 이미지와_함께_상품을_생성하면_대표이미지와_이미지목록이_저장된다() {
        ProductCreateCommand createCommand = generateProductCreateCommandWithImages();
        Product product = Product.create(createCommand, generateIdGenerator());

        assertThat(product.getThumbnailImage()).isNotNull();
        assertThat(product.getImages()).hasSize(createCommand.images().size() - 1);
    }

    @Test
    void 이미지_없이_상품을_생성하면_대표이미지와_이미지목록이_비어있다() {
        Product product = generateProduct();

        assertThat(product.getThumbnailImage()).isNull();
        assertThat(product.getImages()).isEmpty();
    }

    @Test
    void 이미지가_있는데_대표이미지가_없으면_예외가_발생한다() {
        List<ProductImageCommand> nonThumbnailImages = List.of(
                new ProductImageCommand("https://test.com/image1.jpg", false),
                new ProductImageCommand("https://test.com/image2.jpg", false)
        );
        ProductCreateCommand createCommand = generateProductCreateCommand(nonThumbnailImages);

        assertThatThrownBy(() -> Product.create(createCommand, generateIdGenerator()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 대표이미지_외_이미지가_10개를_초과하면_예외가_발생한다() {
        List<ProductImageCommand> images = new ArrayList<>();
        images.add(new ProductImageCommand("https://test.com/thumbnail.jpg", true));
        for (int i = 0; i < 11; i++) {
            images.add(new ProductImageCommand("https://test.com/image" + i + ".jpg", false));
        }
        ProductCreateCommand createCommand = generateProductCreateCommand(images);

        assertThatThrownBy(() -> Product.create(createCommand, generateIdGenerator()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이미_DISCONTINUED_상태에서_판매_중단하면_예외가_발생한다() {
        // given
        Product product = generateDiscontinuedProduct();

        // when
        // then
        assertThatThrownBy(product::discontinue)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 이미지를_추가하면_식별자와_URL이_저장된다() {
        // given
        Product product = generateProduct();
        String imageUrl = "https://test.com/image.jpg";

        // when
        product.addImages(List.of(new AddProductImageCommand(imageUrl)), generateIdGenerator());

        // then
        assertThat(product.getImages()).hasSize(1);
        assertThat(product.getImages().getFirst().id()).isNotNull();
        assertThat(product.getImages().getFirst().imageUrl().value()).isEqualTo(imageUrl);
    }
    
    @Test
    void 대표이미지가_없는_상태에서_이미지_목록을_추가하면_첫_번째_이미지가_자동으로_대표이미지가_된다() {
        // given
        Product product = generateProduct();
        List<AddProductImageCommand> addImageCommands = ProductFixture.generateAddProductImageCommand(3);

        // when
        product.addImages(addImageCommands, generateIdGenerator());
        
        // then
        assertThat(product.getThumbnailImage()).isNotNull();
        assertThat(product.getThumbnailImage().imageUrl().value()).isEqualTo(addImageCommands.getFirst().imageUrl());
    }

}