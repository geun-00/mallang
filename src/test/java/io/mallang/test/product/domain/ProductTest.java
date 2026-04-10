package io.mallang.test.product.domain;

import io.mallang.annotations.DomainTest;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.exception.InvalidValueException;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.*;
import io.mallang.product.domain.command.AddProductImageCommand;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.CreateProductImageCommand;
import io.mallang.product.domain.command.ModifyProductCommand;
import io.mallang.product.domain.exception.ProductImageNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.mallang.assertions.ProductAssertions.isDerivedFrom;
import static io.mallang.fixtures.ProductFixture.*;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DomainTest
@DisplayName("Product 엔티티")
class ProductTest {

    @Nested
    class 생성 {

        @Test
        void 유효한_정보로_상품을_생성하면_식별자가_할당된다() {
            Product product = generateProduct();

            assertThat(product).isNotNull();
            assertThat(product.getId()).isNotNull();
            assertThat(product.getId().value()).isNotNull();
        }

        @Test
        void 유효한_정보로_상품을_생성하면_상품명_설명_가격_재고_수량_카테고리가_저장된다() {
            CreateProductCommand createCommand = generateProductCreateCommand();
            IdGenerator idGenerator = generateIdGenerator();

            Product product = Product.create(createCommand, generateSellerId(), idGenerator);

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
    }

    @Nested
    class 판매자_판별 {

        @Test
        void 판매자_본인이면_isSeller가_true를_반환한다() {
            MemberId sellerId = generateSellerId();
            Product product = Product.create(generateProductCreateCommand(), sellerId, generateIdGenerator());

            assertThat(product.isSeller(sellerId)).isTrue();
        }

        @Test
        void 판매자가_아니면_isSeller가_false를_반환한다() {
            Product product = Product.create(generateProductCreateCommand(), generateSellerId(), generateIdGenerator());

            assertThat(product.isSeller(generateSellerId())).isFalse();
        }
    }

    @Nested
    class 재고 {

        @Test
        void 재고를_추가하면_수량이_증가한다() {
            Product product = generateProduct();
            int oldStockQuantity = product.getStockQuantity().value();

            product.addStock(3);

            assertThat(product.getStockQuantity().value()).isEqualTo(oldStockQuantity + 3);
        }

        @Test
        void 재고를_차감하면_수량이_감소한다() {
            Product product = generateProduct();
            int oldStockQuantity = product.getStockQuantity().value();

            product.deductStock(3);

            assertThat(product.getStockQuantity().value()).isEqualTo(oldStockQuantity - 3);
        }

        @Test
        void 재고_차감_후_0이_되면_SOLD_OUT_상태가_된다() {
            Product product = generateProduct(3);

            product.deductStock(3);

            assertThat(product.getStockQuantity().value()).isZero();
            assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
        }

        @Test
        void 재고_추가_후_1_이상이_되면_ON_SALE_상태가_된다() {
            Product product = generateProduct(0);

            product.addStock(1);

            assertThat(product.getStockQuantity().value()).isEqualTo(1);
            assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        }

        @Test
        void 재고_차감_시_보유_재고보다_많은_수량을_차감하면_예외가_발생한다() {
            Product product = generateProduct(2);

            assertThatThrownBy(() -> product.deductStock(3))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 확인_대상_수량이_재고보다_많으면_예외가_발생한다() {
            Product product = generateProduct(2);

            assertThatThrownBy(() -> product.validateEnoughStock(3))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 충분한_재고가_있으면_확인만_하고_재고는_변하지_않는다() {
            Product product = generateProduct(5);

            product.validateEnoughStock(3);

            assertThat(product.getStockQuantity().value()).isEqualTo(5);
        }

        @Test
        void DISCONTINUED_상품은_재고를_추가할_수_없다() {
            Product product = generateDiscontinuedProduct();

            assertThatThrownBy(() -> product.addStock(1))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void DISCONTINUED_상품은_재고를_차감할_수_없다() {
            Product product = generateDiscontinuedProduct();

            assertThatThrownBy(() -> product.deductStock(1))
                    .isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class 수정_및_판매_중단 {

        @Test
        void 상품_정보를_수정할_수_있다() {
            Product product = generateProduct();

            ModifyProductCommand modifyCommand = generateModifyProductCommand();
            product.modify(modifyCommand);

            assertThat(product).satisfies(isDerivedFrom(modifyCommand));
        }

        @Test
        void 판매_중단하면_DISCONTINUED_상태가_된다() {
            Product product = generateProduct();

            product.discontinue();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.DISCONTINUED);
        }

        @Test
        void DISCONTINUED_상품은_수정할_수_없다() {
            Product product = generateDiscontinuedProduct();

            assertThatThrownBy(() -> product.modify(generateModifyProductCommand()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 이미_DISCONTINUED_상태에서_판매_중단하면_예외가_발생한다() {
            Product product = generateDiscontinuedProduct();

            assertThatThrownBy(product::discontinue)
                    .isInstanceOf(InvalidValueException.class);
        }
    }

    @Nested
    class 이미지 {

        @Test
        void 이미지와_함께_상품을_생성하면_대표이미지와_이미지목록이_저장된다() {
            CreateProductCommand createCommand = generateProductCreateCommandWithImages();
            Product product = Product.create(createCommand, generateSellerId(), generateIdGenerator());

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
            List<CreateProductImageCommand> nonThumbnailImages = List.of(
                    new CreateProductImageCommand(new ImageUrl("https://test.com/image1.jpg"), false),
                    new CreateProductImageCommand(new ImageUrl("https://test.com/image2.jpg"), false)
            );
            CreateProductCommand createCommand = generateProductCreateCommand(nonThumbnailImages);

            assertThatThrownBy(() -> Product.create(createCommand, generateSellerId(), generateIdGenerator()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 대표이미지_외_이미지가_10개를_초과하면_예외가_발생한다() {
            CreateProductCommand createCommand = generateProductCreateCommandWithImages(11);

            assertThatThrownBy(() -> Product.create(createCommand, generateSellerId(), generateIdGenerator()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 이미지를_추가하면_식별자와_URL이_저장된다() {
            Product product = generateProduct();
            String imageUrl = "https://test.com/image.jpg";

            product.addImages(List.of(new AddProductImageCommand(new ImageUrl(imageUrl))), generateIdGenerator());

            ProductImage productImage = product.getThumbnailImage();
            assertThat(productImage).isNotNull();
            assertThat(productImage.id()).isNotNull();
            assertThat(productImage.imageUrl().value()).isEqualTo(imageUrl);
        }

        @Test
        void 이미지가_없는_상태에서_이미지_목록을_추가하면_첫_번째_이미지가_대표이미지가_된다() {
            Product product = generateProduct();
            List<AddProductImageCommand> addImageCommands = generateAddProductImageCommand(3);

            product.addImages(addImageCommands, generateIdGenerator());

            assertThat(product.getThumbnailImage()).isNotNull();
            assertThat(product.getThumbnailImage().imageUrl().value()).isEqualTo(addImageCommands.getFirst().imageUrl().value());
        }

        @Test
        void 대표이미지가_있는_상태에서_이미지_목록을_추가하면_모든_이미지는_일반_이미지로_추가된다() {
            Product product = generateProductWithImages();
            ProductImage originalThumbnail = product.getThumbnailImage();
            int originalImageCount = product.getImages().size();

            List<AddProductImageCommand> addImageCommands = generateAddProductImageCommand(3);
            product.addImages(addImageCommands, generateIdGenerator());

            assertThat(product.getThumbnailImage()).isEqualTo(originalThumbnail);
            assertThat(product.getImages()).hasSize(originalImageCount + addImageCommands.size());
            assertThat(product.getImages())
                    .extracting(ProductImage::imageUrl)
                    .extracting(ImageUrl::value)
                    .containsAll(addImageCommands.stream().map(command -> command.imageUrl().value()).toList());
        }

        @Test
        void 대표이미지_외_이미지는_최대_10개까지만_추가할_수_있다() {
            Product product = generateProductWithImages(8);
            List<AddProductImageCommand> addImageCommands = generateAddProductImageCommand(3);

            assertThatThrownBy(() -> product.addImages(addImageCommands, generateIdGenerator()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 빈_이미지_목록을_추가하면_아무_변화가_없다() {
            Product product = generateProductWithImages();
            ProductImage originalThumbnail = product.getThumbnailImage();
            int originalSize = product.getImages().size();

            product.addImages(List.of(), generateIdGenerator());

            assertThat(product.getThumbnailImage()).isEqualTo(originalThumbnail);
            assertThat(product.getImages()).hasSize(originalSize);
        }

        @Test
        void DISCONTINUED_상품은_이미지를_추가할_수_없다() {
            Product product = generateDiscontinuedProduct();

            assertThatThrownBy(() -> product.addImages(generateAddProductImageCommand(1), generateIdGenerator()))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 일반_이미지를_삭제할_수_있다() {
            Product product = generateProductWithImages();
            int originalSize = product.getImages().size();

            ProductImage targetImage = product.getImages().getFirst();
            ProductImageId targetId = targetImage.id();

            product.removeImage(targetId);

            assertThat(product.getImages()).hasSize(originalSize - 1);
            assertThat(product.getImages()).doesNotContain(targetImage);
        }

        @Test
        void 대표이미지만_있는_상태에서_대표이미지를_삭제하면_이미지가_없는_상태가_된다() {
            Product product = generateProductWithImages(0);
            ProductImage targetImage = product.getThumbnailImage();

            product.removeImage(targetImage.id());

            assertThat(product.getThumbnailImage()).isNull();
            assertThat(product.getImages()).isEmpty();
        }

        @Test
        void 대표이미지를_삭제하면_남은_이미지_중_첫_번째가_대표이미지가_된다() {
            Product product = generateProductWithImages(3);
            int originalImagesCount = product.getImages().size();
            ProductImage originalThumbnailImage = product.getThumbnailImage();
            ProductImage expectedNextThumbnailImage = product.getImages().getFirst();

            product.removeImage(originalThumbnailImage.id());

            assertThat(product.getThumbnailImage()).isNotNull();
            assertThat(product.getThumbnailImage()).isNotEqualTo(originalThumbnailImage);
            assertThat(product.getThumbnailImage()).isEqualTo(expectedNextThumbnailImage);
            assertThat(product.getImages()).hasSize(originalImagesCount - 1);
            assertThat(product.getImages()).doesNotContain(expectedNextThumbnailImage);
        }

        @Test
        void 존재하지_않는_이미지_ID로_삭제하면_예외가_발생한다() {
            Product product = generateProductWithImages(3);
            ProductImageId wrongTargetId = new ProductImageId(randomUUID().toString());

            assertThatThrownBy(() -> product.removeImage(wrongTargetId))
                    .isInstanceOf(ProductImageNotFoundException.class);
        }

        @Test
        void DISCONTINUED_상품은_이미지를_삭제할_수_없다() {
            Product product = generateProductWithImages();
            ProductImageId targetId = product.getThumbnailImage().id();
            product.discontinue();

            assertThatThrownBy(() -> product.removeImage(targetId))
                    .isInstanceOf(InvalidValueException.class);
        }

        @Test
        void 대표이미지를_변경하면_기존_대표이미지는_일반_이미지로_전환된다() {
            Product product = generateProductWithImages(5);
            ProductImage originalThumbnail = product.getThumbnailImage();
            ProductImage targetImage = product.getImages().getFirst();

            product.changeThumbnailImage(targetImage.id());

            assertThat(product.getThumbnailImage()).isNotNull();
            assertThat(product.getThumbnailImage()).isNotEqualTo(originalThumbnail);
            assertThat(product.getThumbnailImage()).isEqualTo(targetImage);
            assertThat(product.getImages()).contains(originalThumbnail);
            assertThat(product.getImages()).doesNotContain(targetImage);
        }

        @Test
        void 대표이미지로_변경하려는_이미지가_존재하지_않으면_예외가_발생한다() {
            Product product = generateProductWithImages(5);

            assertThatThrownBy(() -> product.changeThumbnailImage(new ProductImageId(randomUUID().toString())))
                    .isInstanceOf(ProductImageNotFoundException.class);
        }

        @Test
        void 이미_대표이미지인_이미지를_대표이미지로_변경하면_상태는_그대로_유지된다() {
            Product product = generateProductWithImages(5);
            ProductImage originalThumbnail = product.getThumbnailImage();

            product.changeThumbnailImage(originalThumbnail.id());

            assertThat(product.getThumbnailImage()).isNotNull();
            assertThat(product.getThumbnailImage()).isEqualTo(originalThumbnail);
        }

        @Test
        void DISCONTINUED_상품은_대표이미지를_변경할_수_없다() {
            Product product = generateProductWithImages(5);
            ProductImage targetImage = product.getThumbnailImage();
            product.discontinue();

            assertThatThrownBy(() -> product.changeThumbnailImage(targetImage.id()))
                    .isInstanceOf(InvalidValueException.class);
        }
    }
}
