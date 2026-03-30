package io.mallang.test.member.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.domain.ShippingAddressId;
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

import static io.mallang.fixtures.MemberFixture.generateRegisterShippingAddressRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("POST /my/shipping-addresses")
class ShippingAddressCommandApi_POST {

    @Test
    void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = generateRegisterShippingAddressRequest();

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = generateRegisterShippingAddressRequest();

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        URI location = response.getHeaders().getLocation();
        assertThat(location).isNotNull();

        String id = location.getPath().substring("/my/shipping-addresses/".length());
        assertThatCode(() -> new ShippingAddressId(id))
                .doesNotThrowAnyException();
    }

    @Test
    void detailAddress_속성이_없어도_201_Created_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = new RegisterShippingAddressRequest(
                "홍길동",
                "01011112222",
                "12345",
                "서울시 강남구 테헤란로 1",
                null
        );

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        var request = generateRegisterShippingAddressRequest();

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().postForEntity(
                "/my/shipping-addresses",
                request,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void receiverName_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String receiverName,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new RegisterShippingAddressRequest(
                receiverName,
                "01011112222",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String receiverPhoneNumber,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new RegisterShippingAddressRequest(
                "홍길동",
                receiverPhoneNumber,
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void zipCode_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String zipCode,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new RegisterShippingAddressRequest(
                "홍길동",
                "01011112222",
                zipCode,
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void mainAddress_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
            String mainAddress,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        var request = new RegisterShippingAddressRequest(
                "홍길동",
                "01011112222",
                "12345",
                mainAddress,
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String invalidName = "   홍길동   ";
        var request = new RegisterShippingAddressRequest(
                invalidName,
                "01011112222",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 배송지를_6개째_등록하면_400_Bad_Request_상태코드를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        var request = generateRegisterShippingAddressRequest();

        for (int i = 0; i < 5; i++) {
            fixture.registerShippingAddress(request);
        }

        // when
        ResponseEntity<Void> response = fixture.registerShippingAddress(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}
