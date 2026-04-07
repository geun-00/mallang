package io.mallang.test.product.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.product.application.provided.query.model.ProductDetailView;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductDetailPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static io.mallang.fixtures.MemberFixture.generateMemberWithNickname;
import static io.mallang.fixtures.ProductFixture.generateProductWithImages;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PortTest
@DisplayName("LoadProductDetail Port")
class LoadProductDetailPortTest {

    @Test
    void 상품_상세를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadProductDetailPort loadProductDetailPort
    ) {
        Member seller = generateMemberWithNickname("alice");
        saveMemberPort.save(seller);

        Product product = generateProductWithImages(
                seller.getId(),
                "Apple",
                BigDecimal.valueOf(3000),
                ProductCategory.FOOD,
                2
        );
        saveProductPort.save(product);

        ProductDetailView result = loadProductDetailPort.load(product.getId().value());

        assertThat(result.productId()).isEqualTo(product.getId().value());
        assertThat(result.sellerIdValue()).isEqualTo(seller.getId().value());
        assertThat(result.sellerNickname()).isEqualTo(seller.getNickname().value());
        assertThat(result.name()).isEqualTo(product.getName().value());
        assertThat(result.thumbnailImageUrl()).isNotBlank();
        assertThat(result.images()).hasSize(3);
        assertThat(result.images().getFirst().thumbnail()).isTrue();
    }

    @Test
    void 존재하지_않는_상품이면_ProductNotFoundException이_발생한다(
            @Autowired LoadProductDetailPort loadProductDetailPort
    ) {
        assertThatThrownBy(() -> loadProductDetailPort.load("unknown"))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
