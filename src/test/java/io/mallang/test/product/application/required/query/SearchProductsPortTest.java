package io.mallang.test.product.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.application.shared.query.SliceResult;
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

import static io.mallang.fixtures.MemberFixture.generateMemberWithNickname;
import static io.mallang.fixtures.ProductFixture.generateProduct;
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
        Member seller = generateMemberWithNickname("alice");
        saveMemberPort.save(seller);

        saveProductPort.save(generateProduct(seller.getId(), "Apple", BigDecimal.valueOf(3000), ProductCategory.FOOD));
        saveProductPort.save(generateProduct(seller.getId(), "Banana", BigDecimal.valueOf(2000), ProductCategory.FOOD));

        Member otherSeller = generateMemberWithNickname("bob");
        saveMemberPort.save(otherSeller);
        saveProductPort.save(generateProduct(otherSeller.getId(), "Book", BigDecimal.valueOf(15000), ProductCategory.BOOKS));

        SliceResult<ProductListView> result = searchProductsPort.search(
                new SearchProductsQuery(
                        "ali",
                        "App",
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(5000),
                        "FOOD",
                        null,
                        20
                )
        );

        assertThat(result.items()).extracting(ProductListView::name).containsExactly("Apple");
        assertThat(result.items()).extracting(ProductListView::sellerNickname).containsExactly("alice");
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void lastProductId_기준으로_다음_슬라이스를_조회할_수_있다(
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort,
            @Autowired SearchProductsPort searchProductsPort
    ) {
        Member seller = generateMemberWithNickname("charlie");
        saveMemberPort.save(seller);

        saveProductPort.save(generateProduct(seller.getId(), "ProductA", BigDecimal.valueOf(1000), ProductCategory.FOOD));
        saveProductPort.save(generateProduct(seller.getId(), "ProductB", BigDecimal.valueOf(2000), ProductCategory.FOOD));
        saveProductPort.save(generateProduct(seller.getId(), "ProductC", BigDecimal.valueOf(3000), ProductCategory.FOOD));

        SliceResult<ProductListView> firstSlice = searchProductsPort.search(
                new SearchProductsQuery(
                        "charlie",
                        null,
                        null,
                        null,
                        null,
                        null,
                        2
                )
        );

        SliceResult<ProductListView> secondSlice = searchProductsPort.search(
                new SearchProductsQuery(
                        "charlie",
                        null,
                        null,
                        null,
                        null,
                        firstSlice.nextCursor(),
                        2
                )
        );

        assertThat(firstSlice.items()).hasSize(2);
        assertThat(firstSlice.hasNext()).isTrue();
        assertThat(firstSlice.nextCursor()).isEqualTo(firstSlice.items().getLast().productId());

        assertThat(secondSlice.items()).hasSize(1);
        assertThat(secondSlice.hasNext()).isFalse();
        assertThat(secondSlice.nextCursor()).isNull();
        assertThat(secondSlice.items())
                .extracting(ProductListView::productId)
                .doesNotContainAnyElementsOf(firstSlice.items().stream().map(ProductListView::productId).toList());
    }
}
