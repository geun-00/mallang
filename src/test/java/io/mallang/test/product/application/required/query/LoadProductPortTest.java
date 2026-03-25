package io.mallang.test.product.application.required.query;

import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.mallang.fixtures.ProductFixture.generateProduct;
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