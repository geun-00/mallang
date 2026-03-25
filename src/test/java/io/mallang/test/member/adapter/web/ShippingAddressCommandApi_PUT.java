package io.mallang.test.member.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.MemberFixture.generateUpdateShippingAddressRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("PUT /my/shipping-addresses/{id}")
class ShippingAddressCommandApi_PUT {

    @Test
    void 올바르게_요청하면_204_No_Content를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        var request = generateUpdateShippingAddressRequest();

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void detailAddress_속성이_없어도_204_No_Content를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        var request = new UpdateShippingAddressRequest(
                "이순신",
                "01022223333",
                "13579",
                "경기도 부천시 원미구",
                null
        );

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        var request = generateUpdateShippingAddressRequest();

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                RequestEntity.put("/my/shipping-addresses/" + id).body(request),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(FOUND);
    }

    @Test
    void 존재하지_않는_배송지_ID이면_404_Not_Found를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String nonExistentId = "non-existent-id";
        var request = generateUpdateShippingAddressRequest();

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(nonExistentId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void receiverName_속성이_지정되지_않으면_400_Bad_Request를_반환한다(
            String receiverName,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        var request = new UpdateShippingAddressRequest(
                receiverName,
                "01022223333",
                "13579",
                "경기도 부천시 원미구",
                "2층"
        );

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request를_반환한다(
            String receiverPhoneNumber,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        var request = new UpdateShippingAddressRequest(
                "이순신",
                receiverPhoneNumber,
                "13579",
                "경기도 부천시 원미구",
                "2층"
        );

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void zipCode_속성이_지정되지_않으면_400_Bad_Request를_반환한다(
            String zipCode,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        var request = new UpdateShippingAddressRequest(
                "이순신",
                "01022223333",
                zipCode,
                "경기도 부천시 원미구",
                "2층"
        );

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" "})
    void mainAddress_속성이_지정되지_않으면_400_Bad_Request를_반환한다(
            String mainAddress,
            @Autowired TestFixture fixture
    ) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        var request = new UpdateShippingAddressRequest(
                "이순신",
                "01022223333",
                "13579",
                mainAddress,
                "2층"
        );

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void 도메인_규칙을_위반하면_400_Bad_Request를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String id = fixture.registerShippingAddressThenGetId();
        String invalidName = "   이순신   ";
        var request = new UpdateShippingAddressRequest(
                invalidName,
                "01022223333",
                "13579",
                "경기도 부천시 원미구",
                "2층"
        );

        // when
        ResponseEntity<Void> response = fixture.updateShippingAddress(id, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    }
}

