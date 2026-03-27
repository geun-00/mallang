package io.mallang.test.order.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.member.adapter.web.model.MemberCreateRequest;
import io.mallang.member.application.required.command.SaveMemberPort;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Email;
import io.mallang.member.domain.Member;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.OrderId;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("POST /my/orders")
class OrderCommandApi_POST {

    @Test
    void 올바르게_요청하면_201_Created_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();
        Product product = generateProduct(5);
        saveProductPort.save(product);
        CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();
        Product product = generateProduct(5);
        saveProductPort.save(product);
        CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.getPath()).startsWith("/my/orders/");
        assertThat(location.getPath().replace("/my/orders/", "")).isNotBlank();
    }

    @Test
    void 올바르게_요청하면_Location_헤더의_식별자로_주문을_조회할_수_있다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort,
            @Autowired LoadOrderPort loadOrderPort
    ) {
        // given
        fixture.createMemberThenLogin();
        Product product = generateProduct(5);
        saveProductPort.save(product);
        CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 2);

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        String orderIdValue = response.getHeaders().getLocation().getPath().substring("/my/orders/".length());
        assertThatCode(() -> loadOrderPort.getById(new OrderId(orderIdValue)))
                .doesNotThrowAnyException();
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        CreateOrderRequest request = generateCreateOrderRequest("product-id", 2);

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().postForEntity(
                "/my/orders",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @Test
    void items_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                null,
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void items_요소가_null이면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
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

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void item_productId가_null_또는_비어있으면_400_Bad_Request_상태코드를_반환한다(
            String invalidProductId,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest(invalidProductId, 1)),
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void item_quantity가_null이면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest("product-id", null)),
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void item_quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
            int invalidQuantity,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest("product-id", invalidQuantity)),
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void receiverName_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidReceiverName,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest("product-id", 1)),
                invalidReceiverName,
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidReceiverPhoneNumber,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest("product-id", 1)),
                "홍길동",
                invalidReceiverPhoneNumber,
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void zipCode_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidZipCode,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest("product-id", 1)),
                "홍길동",
                "01012345678",
                invalidZipCode,
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void mainAddress_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String invalidMainAddress,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new CreateOrderItemRequest("product-id", 1)),
                "홍길동",
                "01012345678",
                "12345",
                invalidMainAddress,
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 존재하지_않는_상품이면_404_Not_Found_상태코드를_반환한다(
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        CreateOrderRequest request = generateCreateOrderRequest("unknown-product-id", 2);

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void 주문할_수_없는_회원이면_403_Forbidden_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired LoadMemberPort loadMemberPort,
            @Autowired SaveMemberPort saveMemberPort,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        MemberCreateRequest memberRequest = generateCreateRequest();
        fixture.registerMember(memberRequest);

        Member member = loadMemberPort.getByEmail(new Email(memberRequest.email()));
        member.withdraw(generateClockHolder());
        saveMemberPort.save(member);

        fixture.login(memberRequest.email(), memberRequest.password());

        Product product = generateProduct(5);
        saveProductPort.save(product);
        CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 1);

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(
            @Autowired TestFixture fixture,
            @Autowired SaveProductPort saveProductPort
    ) {
        // given
        fixture.createMemberThenLogin();
        Product product = generateProduct(5);
        saveProductPort.save(product);
        CreateOrderRequest request = generateCreateOrderRequest(product.getId().value(), 6);

        // when
        ResponseEntity<Void> response = fixture.createOrder(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

}
