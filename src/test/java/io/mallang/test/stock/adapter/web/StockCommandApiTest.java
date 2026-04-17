package io.mallang.test.stock.adapter.web;

import io.mallang.annotations.WebAdapterTest;
import io.mallang.fixtures.api.FixtureSession;
import io.mallang.fixtures.api.FixtureSessionFactory;
import io.mallang.stock.adapter.web.model.AddStockRequest;
import io.mallang.stock.adapter.web.model.DeductStockRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.ProductFixture.generateAddStockRequest;
import static io.mallang.fixtures.api.ApiFixture.STOCKS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@WebAdapterTest
@DisplayName("StockCommand API")
class StockCommandApiTest {

    @Nested
    @DisplayName("POST /stocks/{productId}/add")
    class 재고_추가 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = fixture.stock().addStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = fixture.stock()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.post(STOCKS_API + "/" + productId + "/add")
                                                                            .body(request),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = fixture.stock().addStock("non-existent-product-id", request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired FixtureSessionFactory fixtureSessionFactory
            ) {
                FixtureSession anotherFixture = fixtureSessionFactory.create();

                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                anotherFixture.auth().createMemberThenLogin();
                var request = generateAddStockRequest();

                ResponseEntity<Void> response = anotherFixture.stock().addStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("POST /stocks/{productId}/deduct")
    class 재고_차감 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.stock().addStock(productId, new AddStockRequest(50));
                var request = new DeductStockRequest(5);

                ResponseEntity<Void> response = fixture.stock().deductStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_401_Unauthorized_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                var request = new DeductStockRequest(1);

                ResponseEntity<Void> response = fixture.stock()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.post(STOCKS_API + "/" + productId + "/deduct")
                                                                            .body(request),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                var request = new DeductStockRequest(1);

                ResponseEntity<Void> response = fixture.stock().deductStock("non-existent-product-id", request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();

                var request = new DeductStockRequest(Integer.MAX_VALUE);

                ResponseEntity<Void> response = fixture.stock().deductStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_상품이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired FixtureSessionFactory fixtureSessionFactory
            ) {
                FixtureSession anotherFixture = fixtureSessionFactory.create();

                fixture.auth().createMemberThenLogin();
                String productId = fixture.product().registerProductThenGetId();
                fixture.stock().addStock(productId, new AddStockRequest(50));

                anotherFixture.auth().createMemberThenLogin();
                var request = new DeductStockRequest(5);

                ResponseEntity<Void> response = anotherFixture.stock().deductStock(productId, request);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }
}
