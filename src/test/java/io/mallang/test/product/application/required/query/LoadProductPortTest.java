package io.mallang.test.product.application.required.query;

import io.mallang.domain.common.exception.AggregateNotLoadedException;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.command.AddProductImageCommand;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoadProductPortTest {

    @Test
    void getById로_Product를_조회한다(
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProduct();
        saveProductPort.save(product);

        // when & then
        assertThatCode(() -> loadProductPort.getById(product.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void getById는_존재하지_않는_ID로_조회하면_ProductNotFoundException이_발생한다(
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        ProductId unknownId = new ProductId("unknown");

        // when & then
        assertThatThrownBy(() -> loadProductPort.getById(unknownId))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getById로_조회한_Product는_이미지_관련_기능을_사용할_수_없다(
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProductWithImages();
        saveProductPort.save(product);

        Product loaded = loadProductPort.getById(product.getId());

        // when & then
        assertThatThrownBy(() -> loaded.addImages(List.of(new AddProductImageCommand("https://test.com/new-image.jpg")), () -> "new-id"))
                .isInstanceOf(AggregateNotLoadedException.class);
    }

    @Test
    void getByIdWithImages로_Product를_조회한다(
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProduct();
        saveProductPort.save(product);

        // when & then
        assertThatCode(() -> loadProductPort.getByIdWithImages(product.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    void getByIdWithImages는_존재하지_않는_ID로_조회하면_ProductNotFoundException이_발생한다(
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        ProductId unknownId = new ProductId("unknown");

        // when & then
        assertThatThrownBy(() -> loadProductPort.getByIdWithImages(unknownId))
                .isInstanceOf(ProductNotFoundException.class);
    }
}