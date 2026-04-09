package io.mallang.test.product.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.common.application.query.SliceResult;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.SearchProductsPort;
import io.mallang.product.domain.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@PortTest
@DisplayName("SearchProducts Port")
class SearchProductsPortTest {

    @Test
    void 작성자명_상품명_가격범위_카테고리로_검색할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SearchProductsPort searchProductsPort
    ) {
        Member seller = generateMember();
        saveMemberPort.save(seller);

        saveProductPort.save(generateProduct(seller.getId(), generateProductName(), BigDecimal.valueOf(2000), ProductCategory.FOOD));
        saveProductPort.save(generateProduct(seller.getId(), generateProductName(), BigDecimal.valueOf(3000), ProductCategory.FOOD));
        saveProductPort.save(generateProduct(seller.getId(), generateProductName(), BigDecimal.valueOf(15000), ProductCategory.BOOKS));

        SliceResult<ProductListView> result = searchProductsPort.search(new SearchProductsQuery(
                seller.getNickname().value(),
                null,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5000),
                "FOOD",
                null,
                20
        ));

        assertThat(result.items()).extracting(ProductListView::category)
                                  .contains(ProductCategory.FOOD.name())
                                  .doesNotContain(ProductCategory.BOOKS.name());
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void lastProductId_기준으로_다음_슬라이스를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SearchProductsPort searchProductsPort
    ) {
        Member seller = generateMember();
        saveMemberPort.save(seller);

        saveProductPort.save(generateProduct(seller.getId(), generateProductName(), generateProductPriceAmount(), ProductCategory.FOOD));
        saveProductPort.save(generateProduct(seller.getId(), generateProductName(), generateProductPriceAmount(), ProductCategory.FOOD));
        saveProductPort.save(generateProduct(seller.getId(), generateProductName(), generateProductPriceAmount(), ProductCategory.FOOD));

        String sellerNickname = seller.getNickname().value();

        SliceResult<ProductListView> firstSlice = searchProductsPort.search(new SearchProductsQuery(
                sellerNickname, null, null, null, null, null, 2
        ));

        SliceResult<ProductListView> secondSlice = searchProductsPort.search(new SearchProductsQuery(
                sellerNickname, null, null, null, null, firstSlice.nextCursor(), 2
        ));

        assertThat(firstSlice.items()).hasSize(2);
        assertThat(firstSlice.hasNext()).isTrue();
        assertThat(firstSlice.nextCursor()).isEqualTo(firstSlice.items().getLast().productId());

        assertThat(secondSlice.items()).hasSize(1);
        assertThat(secondSlice.hasNext()).isFalse();
        assertThat(secondSlice.nextCursor()).isNull();
        assertThat(secondSlice.items()).extracting(ProductListView::productId)
                                       .doesNotContainAnyElementsOf(firstSlice.items()
                                                                              .stream()
                                                                              .map(ProductListView::productId)
                                                                              .toList());
    }
}
