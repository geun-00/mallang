package io.mallang.test.order.adapter.web;

import io.mallang.FixtureSession;
import io.mallang.FixtureSessionFactory;
import io.mallang.annotations.WebAdapterTest;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static io.mallang.fixtures.MemberFixture.generateClockHolder;
import static io.mallang.fixtures.MemberFixture.generateCreateRequest;
import static io.mallang.fixtures.OrderFixture.generateCreateOrderRequest;
import static io.mallang.fixtures.ProductFixture.generateProduct;
import static io.mallang.order.adapter.web.model.CreateOrderRequest.CreateOrderItemRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.http.HttpStatus.*;

@WebAdapterTest
@DisplayName("OrderCommand API")
class OrderCommandApiTest {

    @Nested
    @DisplayName("POST /my/orders")
    class 주문_생성 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_201_Created_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                Product product = generateProduct(5);
                saveProductPort.save(product);
                CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                Product product = generateProduct(5);
                saveProductPort.save(product);
                CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                URI location = response.getHeaders().getLocation();
                assertThat(location).isNotNull();
                assertThat(location.getPath()).startsWith("/my/orders/");
                assertThat(location.getPath().replace("/my/orders/", "")).isNotBlank();
            }

            @Test
            void 올바르게_요청하면_Location_헤더의_식별자로_주문을_조회할_수_있다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired LoadOrderPort loadOrderPort
            ) {
                fixture.auth().createMemberThenLogin();
                Product product = generateProduct(5);
                saveProductPort.save(product);
                CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);

                String orderIdValue = fixture.order().createOrderThenGetId(request);

                assertThatCode(() -> loadOrderPort.getById(new OrderId(orderIdValue))).doesNotThrowAnyException();
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired FixtureSession fixture) {
                CreateOrderRequest request = generateCreateOrderRequest("product-id", 2);

                ResponseEntity<Void> response = fixture.order()
                                                       .unauthenticatedClient()
                                                       .postForEntity("/my/orders", request, Void.class);

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @Test
            void items_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        null,
                        "홍길동",
                        "01012345678",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void items_요소가_null이면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                List<CreateOrderItemRequest> items = new ArrayList<>();
                items.add(new CreateOrderItemRequest("product-id", 1));
                items.add(null);

                CreateOrderRequest request = new CreateOrderRequest(
                        items,
                        "홍길동",
                        "01012345678",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {"", " "})
            void item_productId가_null_또는_비어있으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidProductId,
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest(invalidProductId, 1)),
                        "홍길동",
                        "01012345678",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void item_quantity가_null이면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest("product-id", null)),
                        "홍길동",
                        "01012345678",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @ValueSource(ints = {0, -1})
            void item_quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
                    int invalidQuantity,
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest("product-id", invalidQuantity)),
                        "홍길동",
                        "01012345678",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void receiverName_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidReceiverName,
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest("product-id", 1)),
                        invalidReceiverName,
                        "01012345678",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidReceiverPhoneNumber,
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest("product-id", 1)),
                        "홍길동",
                        invalidReceiverPhoneNumber,
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void zipCode_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidZipCode,
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest("product-id", 1)),
                        "홍길동",
                        "01012345678",
                        invalidZipCode,
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void mainAddress_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String invalidMainAddress,
                    @Autowired FixtureSession fixture
            ) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = new CreateOrderRequest(
                        List.of(new CreateOrderItemRequest("product-id", 1)),
                        "홍길동",
                        "01012345678",
                        "12345",
                        invalidMainAddress,
                        "101호"
                );

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();
                CreateOrderRequest request = generateCreateOrderRequest("unknown-product-id", 2);

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();
                Product product = generateProduct(5);
                saveProductPort.save(product);
                CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 6);

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 주문할_수_없는_회원이면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired LoadMemberPort loadMemberPort,
                    @Autowired SaveMemberPort saveMemberPort,
                    @Autowired SaveProductPort saveProductPort
            ) {
                MemberCreateRequest memberRequest = generateCreateRequest();
                fixture.member().registerMember(memberRequest);

                Member member = loadMemberPort.getByEmail(new Email(memberRequest.email()));
                member.withdraw(generateClockHolder());
                saveMemberPort.save(member);

                fixture.auth().login(memberRequest.email(), memberRequest.password());

                Product product = generateProduct(5);
                saveProductPort.save(product);
                CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 1);

                ResponseEntity<Void> response = fixture.order().createOrder(request);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /my/orders/{orderId}/cancel")
    class 주문_취소 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);
                String orderId = fixture.order().createOrderThenGetId(request);

                ResponseEntity<Void> response = fixture.order().cancelOrder(orderId);

                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                String orderId = fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

                ResponseEntity<Void> response = fixture.order()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.patch("/my/orders/" + orderId + "/cancel")
                                                                            .build(),
                                                               Void.class
                                                       );

                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_주문이면_404_Not_Found_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                fixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = fixture.order().cancelOrder("unknown-order-id");

                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 취소할_수_없는_상태의_주문이면_400_Bad_Request_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired SaveProductPort saveProductPort,
                    @Autowired LoadOrderPort loadOrderPort,
                    @Autowired SaveOrderPort saveOrderPort
            ) {
                fixture.auth().createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                String orderId = fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

                Order order = loadOrderPort.getById(new OrderId(orderId));
                order.nextStatus();
                order.nextStatus();
                saveOrderPort.save(order);

                ResponseEntity<Void> response = fixture.order().cancelOrder(orderId);

                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 권한 {

            @Test
            void 본인_주문이_아니면_403_Forbidden_상태코드를_반환한다(
                    @Autowired FixtureSession fixture,
                    @Autowired FixtureSessionFactory fixtureSessionFactory,
                    @Autowired SaveProductPort saveProductPort
            ) {
                FixtureSession anotherFixture = fixtureSessionFactory.create();

                fixture.auth().createMemberThenLogin();

                Product product = generateProduct(5);
                saveProductPort.save(product);

                String orderId = fixture.order().createOrderThenGetId(generateCreateOrderRequest(product.getId().value(), 2));

                anotherFixture.auth().createMemberThenLogin();

                ResponseEntity<Void> response = anotherFixture.order().cancelOrder(orderId);

                assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
            }
        }
    }
}
