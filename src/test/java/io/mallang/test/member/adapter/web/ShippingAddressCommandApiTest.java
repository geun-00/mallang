package io.mallang.test.member.adapter.web;

import io.mallang.FixtureSession;
import io.mallang.annotations.WebAdapterTest;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import io.mallang.member.domain.ShippingAddressId;
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

import static io.mallang.fixtures.MemberFixture.generateRegisterShippingAddressRequest;
import static io.mallang.fixtures.MemberFixture.generateUpdateShippingAddressRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.http.HttpStatus.*;

@WebAdapterTest
@DisplayName("ShippingAddressCommand API")
class ShippingAddressCommandApiTest {

    @Nested
    @DisplayName("POST /my/shipping-addresses")
    class 배송지_추가 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_201_Created_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                var request = generateRegisterShippingAddressRequest();

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }

            @Test
            void 올바르게_요청하면_식별자가_포함된_Location_헤더를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                var request = generateRegisterShippingAddressRequest();

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                URI location = response.getHeaders().getLocation();
                assertThat(location).isNotNull();

                String id = location.getPath().substring("/my/shipping-addresses/".length());
                assertThatCode(() -> new ShippingAddressId(id)).doesNotThrowAnyException();
            }

            @Test
            void detailAddress_속성이_없어도_201_Created_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                var request = new RegisterShippingAddressRequest("홍길동", "01011112222", "12345", "서울시 강남구 테헤란로 1", null);

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(CREATED);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired FixtureSession fixture) {
                // given
                var request = generateRegisterShippingAddressRequest();

                // when
                ResponseEntity<Void> response = fixture.member()
                                                       .unauthenticatedClient()
                                                       .postForEntity("/my/shipping-addresses", request, Void.class);

                // then
                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void receiverName_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String receiverName,
                    @Autowired FixtureSession fixture
            ) {
                // given
                fixture.auth().createMemberThenLogin();
                var request = new RegisterShippingAddressRequest(
                        receiverName,
                        "01011112222",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String receiverPhoneNumber,
                    @Autowired FixtureSession fixture
            ) {
                // given
                fixture.auth().createMemberThenLogin();
                var request = new RegisterShippingAddressRequest(
                        "홍길동",
                        receiverPhoneNumber,
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void zipCode_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String zipCode, @Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                var request = new RegisterShippingAddressRequest(
                        "홍길동",
                        "01011112222",
                        zipCode,
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void mainAddress_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                    String mainAddress,
                    @Autowired FixtureSession fixture
            ) {
                // given
                fixture.auth().createMemberThenLogin();
                var request = new RegisterShippingAddressRequest(
                        "홍길동",
                        "01011112222",
                        "12345",
                        mainAddress,
                        "101호"
                );

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String invalidName = "   홍길동   ";
                var request = new RegisterShippingAddressRequest(
                        invalidName,
                        "01011112222",
                        "12345",
                        "서울시 강남구 테헤란로 1",
                        "101호"
                );

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @Test
            void 배송지를_6개째_등록하면_400_Bad_Request_상태코드를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();

                for (int i = 0; i < 5; i++) {
                    fixture.member().registerShippingAddress(generateRegisterShippingAddressRequest());
                }

                // when
                ResponseEntity<Void> response = fixture.member().registerShippingAddress(generateRegisterShippingAddressRequest());

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /my/shipping-addresses/{shippingAddressId}/default")
    class 기본_배송지_설정 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String shippingAddressId = fixture.member().registerShippingAddressThenGetId();

                // when
                ResponseEntity<Void> response = fixture.member().makeDefaultShippingAddress(shippingAddressId);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String shippingAddressId = fixture.member().registerShippingAddressThenGetId();

                // when
                ResponseEntity<Void> response = fixture.member()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.patch("/my/shipping-addresses/" + shippingAddressId + "/default")
                                                                            .build(),
                                                               Void.class
                                                       );

                // then
                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_배송지_ID이면_404_Not_Found를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String nonExistentId = "non-existent-id";

                // when
                ResponseEntity<Void> response = fixture.member().makeDefaultShippingAddress(nonExistentId);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("PUT /my/shipping-addresses/{shippingAddressId}")
    class 배송지_정보_수정 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                var request = generateUpdateShippingAddressRequest();

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(id, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }

            @Test
            void detailAddress_속성이_없어도_204_No_Content를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                var request = new UpdateShippingAddressRequest(
                        "이순신",
                        "01022223333",
                        "13579",
                        "경기도 부천시 원미구",
                        null
                );

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(id, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                var request = generateUpdateShippingAddressRequest();

                // when
                ResponseEntity<Void> response = fixture.member()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.put("/my/shipping-addresses/" + id)
                                                                            .body(request),
                                                               Void.class
                                                       );

                // then
                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_배송지_ID이면_404_Not_Found를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String nonExistentId = "non-existent-id";
                var request = generateUpdateShippingAddressRequest();

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(nonExistentId, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }

        @Nested
        class 요청_검증 {

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void receiverName_속성이_지정되지_않으면_400_Bad_Request를_반환한다(
                    String receiverName,
                    @Autowired FixtureSession fixture
            ) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                var request = new UpdateShippingAddressRequest(
                        receiverName,
                        "01022223333",
                        "13579",
                        "경기도 부천시 원미구",
                        "2층"
                );

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(id, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request를_반환한다(
                    String receiverPhoneNumber,
                    @Autowired FixtureSession fixture
            ) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                var request = new UpdateShippingAddressRequest(
                        "이순신",
                        receiverPhoneNumber,
                        "13579",
                        "경기도 부천시 원미구",
                        "2층"
                );

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(id, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void zipCode_속성이_지정되지_않으면_400_Bad_Request를_반환한다(String zipCode, @Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                var request = new UpdateShippingAddressRequest(
                        "이순신",
                        "01022223333",
                        zipCode,
                        "경기도 부천시 원미구",
                        "2층"
                );

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(id, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }

            @ParameterizedTest
            @NullSource
            @ValueSource(strings = {" "})
            void mainAddress_속성이_지정되지_않으면_400_Bad_Request를_반환한다(String mainAddress, @Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                var request = new UpdateShippingAddressRequest(
                        "이순신",
                        "01022223333",
                        "13579",
                        mainAddress,
                        "2층"
                );

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(id, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }

        @Nested
        class 도메인_규칙 {

            @Test
            void 도메인_규칙을_위반하면_400_Bad_Request를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();
                String invalidName = "   이순신   ";
                var request = new UpdateShippingAddressRequest(
                        invalidName,
                        "01022223333",
                        "13579",
                        "경기도 부천시 원미구",
                        "2층"
                );

                // when
                ResponseEntity<Void> response = fixture.member().updateShippingAddress(id, request);

                // then
                assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
            }
        }
    }

    @Nested
    @DisplayName("DELETE /my/shipping-addresses/{shippingAddressId}")
    class 배송지_제거 {

        @Nested
        class 성공 {

            @Test
            void 올바르게_요청하면_204_No_Content를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();

                // when
                ResponseEntity<Void> response = fixture.member().removeShippingAddress(id);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
            }
        }

        @Nested
        class 인증 {

            @Test
            void 인증되지_않은_요청이면_로그인_페이지로_리다이렉트한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String id = fixture.member().registerShippingAddressThenGetId();

                // when
                ResponseEntity<Void> response = fixture.member()
                                                       .unauthenticatedClient()
                                                       .exchange(
                                                               RequestEntity.delete("/my/shipping-addresses/" + id)
                                                                            .build(),
                                                               Void.class
                                                       );

                // then
                assertThat(response.getStatusCode()).isEqualTo(FOUND);
            }
        }

        @Nested
        class 조회_실패 {

            @Test
            void 존재하지_않는_배송지_ID이면_404_Not_Found를_반환한다(@Autowired FixtureSession fixture) {
                // given
                fixture.auth().createMemberThenLogin();
                String nonExistentId = "non-existent-id";

                // when
                ResponseEntity<Void> response = fixture.member().removeShippingAddress(nonExistentId);

                // then
                assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
            }
        }
    }
}
