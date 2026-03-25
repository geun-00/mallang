package io.mallang.test.member.adapter.web;

import io.mallang.TestFixture;
import io.mallang.TestFixtureConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.RequestEntity.patch;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestFixtureConfiguration.class)
@DisplayName("PATCH /my/shipping-addresses")
public class ShippingAddressCommandApi_PATCH {

    @Test
    void 올바르게_요청하면_204_No_Content를_반환한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String shippingAddressId = fixture.registerShippingAddressThenGetId();
        // when
        ResponseEntity<Void> response = fixture.client().exchange(
                patch("/my/shipping-addresses/" + shippingAddressId + "/default").build(),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired TestFixture fixture) {
        // given
        fixture.createMemberThenLogin();
        String shippingAddressId = fixture.registerShippingAddressThenGetId();

        // when
        ResponseEntity<Void> response = fixture.unauthenticatedClient().exchange(
                patch("/my/shipping-addresses/" + shippingAddressId + "/default").build(),
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

        // when
        ResponseEntity<Void> response = fixture.client().exchange(
                patch("/my/shipping-addresses/" + nonExistentId + "/default").build(),
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}
