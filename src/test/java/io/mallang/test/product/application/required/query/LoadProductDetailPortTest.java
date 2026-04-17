package io.mallang.test.product.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductDetailPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.exception.ProductNotFoundException;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.assertions.ProductAssertions.isDerivedFrom;
import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.ProductFixture.generateProductWithSeller;
import static io.mallang.fixtures.StockFixture.generateStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PortTest
@DisplayName("LoadProductDetail Port")
class LoadProductDetailPortTest {

    @Test
    void 상품_상세를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadProductDetailPort loadProductDetailPort
    ) {
        Member seller = generateMember();
        saveMemberPort.save(seller);

        Product product = generateProductWithSeller(seller.getId());
        Stock stock = generateStock(product, 10);
        saveProductPort.save(product);
        saveStockPort.save(stock);

        ProductDetailView result = loadProductDetailPort.load(product.getId().value());

        assertThat(result).isNotNull().satisfies(isDerivedFrom(product, seller, stock));
    }

    @Test
    void 존재하지_않는_상품이면_ProductNotFoundException이_발생한다(
            @Autowired LoadProductDetailPort loadProductDetailPort
    ) {
        assertThatThrownBy(() -> loadProductDetailPort.load("unknown"))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
