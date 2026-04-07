package io.mallang.test.product.adapter.web;

import io.mallang.annotations.WebAdapterTest;
import io.mallang.fixtures.api.FixtureSession;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.product.adapter.web.model.SearchProductsRequest;
import io.mallang.product.adapter.web.model.SearchProductsResponse;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static io.mallang.fixtures.MemberFixture.generateMemberWithNickname;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@WebAdapterTest
@DisplayName("ProductQuery API")
class ProductQueryApiTest {

    @Nested
    @DisplayName("GET /products")
    class 상품_목록_조회 {

        @Nested
        class 성공 {

            @Test
            void 검색_조건으로_상품을_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveMemberPort saveMemberPort,
                    @Autowired SaveProductPort saveProductPort
            ) {
                Member seller = generateMemberWithNickname("alice");
                saveMemberPort.save(seller);
                saveProductPort.save(generateProduct(seller.getId(), "Apple", BigDecimal.valueOf(3000), ProductCategory.FOOD));
                saveProductPort.save(generateProduct(seller.getId(), "Banana", BigDecimal.valueOf(2000), ProductCategory.FOOD));

                Member otherSeller = generateMemberWithNickname("bob");
                saveMemberPort.save(otherSeller);
                saveProductPort.save(generateProduct(otherSeller.getId(), "Book", BigDecimal.valueOf(15000), ProductCategory.BOOKS));

                fixture.auth().createMemberThenLogin();

                ResponseEntity<SearchProductsResponse> response = fixture.product().searchProducts(
                        new SearchProductsRequest(
                                "ali",
                                "App",
                                BigDecimal.valueOf(1000),
                                BigDecimal.valueOf(5000),
                                "FOOD",
                                null,
                                20
                        )
                );

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().items()).hasSize(1);
                assertThat(response.getBody().items().getFirst().sellerNickname()).isEqualTo("alice");
                assertThat(response.getBody().items().getFirst().name()).isEqualTo("Apple");
            }

            @Test
            void size를_지정하면_hasNext와_nextCursor를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveMemberPort saveMemberPort,
                    @Autowired SaveProductPort saveProductPort
            ) {
                Member seller = generateMemberWithNickname("charlie");
                saveMemberPort.save(seller);
                saveProductPort.save(generateProduct(seller.getId(), "ProductA", BigDecimal.valueOf(1000), ProductCategory.FOOD));
                saveProductPort.save(generateProduct(seller.getId(), "ProductB", BigDecimal.valueOf(2000), ProductCategory.FOOD));

                fixture.auth().createMemberThenLogin();

                ResponseEntity<SearchProductsResponse> response = fixture.product().searchProducts(
                        new SearchProductsRequest(
                                "charlie",
                                null,
                                null,
                                null,
                                null,
                                null,
                                1
                        )
                );

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().items()).hasSize(1);
                assertThat(response.getBody().hasNext()).isTrue();
                assertThat(response.getBody().nextCursor()).isNotBlank();
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이어도_상품_목록을_조회할_수_있다(
                    @Autowired FixtureSession fixture
            ) {
                ResponseEntity<String> response = fixture.product()
                                                         .unauthenticatedClient()
                                                         .getForEntity("/products", String.class);

                assertThat(response.getStatusCode()).isEqualTo(OK);
            }
        }

        @Nested
        class 요청_검증 {

            @Test
            void size가_0이면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<SearchProductsResponse> response = fixture.product().searchProducts(
                        new SearchProductsRequest(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                0
                        )
                );

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void minPrice가_음수면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<SearchProductsResponse> response = fixture.product().searchProducts(
                        new SearchProductsRequest(
                                null,
                                null,
                                BigDecimal.valueOf(-1),
                                null,
                                null,
                                null,
                                20
                        )
                );

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }
}
