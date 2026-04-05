package io.mallang.test.product.application.required.command;

import io.mallang.annotations.PortTest;
import io.mallang.assertions.ProductAssertions;
import io.mallang.domain.common.vo.Money;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.ProductDescription;
import io.mallang.product.domain.ProductName;
import io.mallang.product.domain.command.ModifyProductCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static org.assertj.core.api.Assertions.assertThat;

@PortTest
@DisplayName("SaveProduct Port")
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

    @Test
    void 저장한_상품을_수정한_뒤_다시_저장하면_변경사항이_반영된다(
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductPort loadProductPort
    ) {
        // given
        Product product = generateProduct();
        saveProductPort.save(product);

        product.modify(new ModifyProductCommand(
                new ProductName("수정된 상품명"),
                new ProductDescription("수정된 상품 설명"),
                new Money(new BigDecimal("15000")),
                ProductCategory.FOOD
        ));

        // when
        saveProductPort.save(product);

        // then
        assertThat(loadProductPort.getById(product.getId()))
                .isNotNull()
                .satisfies(ProductAssertions.isSameAs(product));
    }
}
