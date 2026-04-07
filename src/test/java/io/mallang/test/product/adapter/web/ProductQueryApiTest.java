package io.mallang.test.product.adapter.web;

import io.mallang.annotations.WebAdapterTest;
import io.mallang.fixtures.api.FixtureSession;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.product.adapter.web.model.ProductDetailResponse;
import io.mallang.product.adapter.web.model.SearchProductsRequest;
import io.mallang.product.adapter.web.model.SearchProductsResponse;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static io.mallang.fixtures.MemberFixture.generateMember;
import static io.mallang.fixtures.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

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
                Member seller = generateMember();
                saveMemberPort.save(seller);

                String prefix = UUID.randomUUID().toString();

                Product a = generateProduct(seller.getId(), generateProductName(), BigDecimal.valueOf(2000), ProductCategory.FOOD);
                Product b = generateProduct(seller.getId(), generateProductName(), BigDecimal.valueOf(3000), ProductCategory.FOOD);
                Product c = generateProduct(seller.getId(), generateProductName(), BigDecimal.valueOf(15000), ProductCategory.BOOKS);
                saveProductPort.save(a);
                saveProductPort.save(b);
                saveProductPort.save(c);

                ResponseEntity<SearchProductsResponse> response = fixture.product()
                                                                         .searchProducts(new SearchProductsRequest(
                                                                                 seller.getNickname().value(),
                                                                                 null,
                                                                                 BigDecimal.valueOf(1000),
                                                                                 BigDecimal.valueOf(5000),
                                                                                 "FOOD"),
                                                                                 null,
                                                                                 20
                                                                         );

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().items()).hasSize(2);
                assertThat(response.getBody().items()).extracting("productId")
                                                      .contains(a.getId().value(), b.getId().value());
            }

            @Test
            void size를_지정하면_hasNext와_nextCursor를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveMemberPort saveMemberPort,
                    @Autowired SaveProductPort saveProductPort
            ) {
                Member seller = generateMember();
                saveMemberPort.save(seller);

                saveProductPort.save(generateProduct(seller.getId(), "ProductA", BigDecimal.valueOf(1000), ProductCategory.FOOD));
                saveProductPort.save(generateProduct(seller.getId(), "ProductB", BigDecimal.valueOf(2000), ProductCategory.FOOD));

                ResponseEntity<SearchProductsResponse> response = fixture.product()
                                                                         .searchProducts(new SearchProductsRequest(
                                                                                 seller.getNickname().value(),
                                                                                 null,
                                                                                 null,
                                                                                 null,
                                                                                 null),
                                                                                 null,
                                                                                 1
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
            void minPrice가_음수면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                ResponseEntity<SearchProductsResponse> response = fixture.product()
                                                                         .searchProducts(new SearchProductsRequest(
                                                                                 null,
                                                                                 null,
                                                                                 BigDecimal.valueOf(-1),
                                                                                 null,
                                                                                 null),
                                                                                 null,
                                                                                 20
                                                                         );

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }

    @Nested
    @DisplayName("GET /products/{productId}")
    class 상품_상세_조회 {

        @Nested
        class 성공 {

            @Test
            void 상품_상세를_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveMemberPort saveMemberPort,
                    @Autowired SaveProductPort saveProductPort
            ) {
                Member seller = generateMember();
                saveMemberPort.save(seller);

                Product product = generateProductWithSeller(seller.getId());
                saveProductPort.save(product);

                ResponseEntity<ProductDetailResponse> response = fixture.product().getProductDetail(product.getId().value());

                assertThat(response.getStatusCode()).isEqualTo(OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().productId()).isEqualTo(product.getId().value());
                assertThat(response.getBody().sellerNickname()).isEqualTo(seller.getNickname().value());
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이어도_상품_상세를_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveMemberPort saveMemberPort,
                    @Autowired SaveProductPort saveProductPort
            ) {
                Member seller = generateMember();
                saveMemberPort.save(seller);

                Product product = generateProductWithSeller(seller.getId());
                saveProductPort.save(product);

                ResponseEntity<String> response = fixture.product()
                                                         .unauthenticatedClient()
                                                         .getForEntity(
                                                                 "/products/" + product.getId().value(),
                                                                 String.class
                                                         );

                assertThat(response.getStatusCode()).isEqualTo(OK);
            }
        }

        @Nested
        class 예외 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
                    @Autowired FixtureSession fixture
            ) {
                ResponseEntity<String> response = fixture.product()
                                                         .unauthenticatedClient()
                                                         .getForEntity("/products/unknown-product-id", String.class);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }
    }
}
