package io.mallang.test.product.application.required.command;

import io.mallang.ProductAssertions;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SaveProductPortTest {

    @Test
    void 저장하면_조회된다(
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProduct();

        // when
        saveProductPort.save(product);

        // then
        assertThat(loadProductPort.getById(product.getId()))
                .isNotNull()
                .satisfies(ProductAssertions.isSameAs(product));
    }

    @Test
    void 이미지와_함께_저장하면_이미지_포함_조회된다(
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProductWithImages();

        // when
        saveProductPort.save(product);

        // then
        assertThat(loadProductPort.getByIdWithImages(product.getId()))
                .isNotNull()
                .satisfies(ProductAssertions.isSameAsWithImages(product));
    }
}