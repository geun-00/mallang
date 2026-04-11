package io.mallang.test.member.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.member.adapter.web.ShippingAddressCommandApi;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import io.mallang.member.application.provided.command.RegisterShippingAddressUseCase;
import io.mallang.member.application.provided.command.RemoveShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateDefaultShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateShippingAddressUseCase;
import io.mallang.test.support.security.WithMockMember;
import io.mallang.test.support.web.WebMvcRequestTestSupport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static io.mallang.fixtures.api.ApiFixture.SHIPPING_ADDRESSES_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcAdapterTest(ShippingAddressCommandApi.class)
class ShippingAddressCommandApiWebMvcTest extends WebMvcRequestTestSupport {

    @MockitoBean
    RegisterShippingAddressUseCase registerShippingAddressUseCase;

    @MockitoBean
    RemoveShippingAddressUseCase removeShippingAddressUseCase;

    @MockitoBean
    UpdateDefaultShippingAddressUseCase updateDefaultShippingAddressUseCase;

    @MockitoBean
    UpdateShippingAddressUseCase updateShippingAddressUseCase;

    @Nested
    class 배송지_추가_요청_검증 {

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void receiverName_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String receiverName) throws JsonProcessingException {
            // given
            var request = new RegisterShippingAddressRequest(
                    receiverName,
                    "01011112222",
                    "12345",
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = postJson(SHIPPING_ADDRESSES_API, request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String receiverPhoneNumber) throws JsonProcessingException {
            // given
            var request = new RegisterShippingAddressRequest(
                    "홍길동",
                    receiverPhoneNumber,
                    "12345",
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = postJson(SHIPPING_ADDRESSES_API, request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void zipCode_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String zipCode) throws JsonProcessingException {
            // given
            var request = new RegisterShippingAddressRequest("홍길동", "01011112222", zipCode, "서울시 강남구 테헤란로 1", "101호");

            // when
            MvcTestResult result = postJson(SHIPPING_ADDRESSES_API, request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void mainAddress_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(String mainAddress) throws JsonProcessingException {
            // given
            var request = new RegisterShippingAddressRequest("홍길동", "01011112222", "12345", mainAddress, "101호");

            // when
            MvcTestResult result = postJson(SHIPPING_ADDRESSES_API, request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }
    }

    @Nested
    class 배송지_정보_수정_요청_검증 {

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void receiverName_속성이_지정되지_않으면_400_Bad_Request를_반환한다(String receiverName) throws JsonProcessingException {
            // given
            var request = new UpdateShippingAddressRequest(receiverName, "01022223333", "13579", "경기도 부천시 원미구", "2층");

            // when
            MvcTestResult result = putJson(SHIPPING_ADDRESSES_API + "/" + "shipping-address-id", request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request를_반환한다(String receiverPhoneNumber) throws JsonProcessingException {
            // given
            var request = new UpdateShippingAddressRequest("이순신", receiverPhoneNumber, "13579", "경기도 부천시 원미구", "2층");

            // when
            MvcTestResult result = putJson(SHIPPING_ADDRESSES_API + "/" + "shipping-address-id", request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void zipCode_속성이_지정되지_않으면_400_Bad_Request를_반환한다(String zipCode) throws JsonProcessingException {
            // given
            var request = new UpdateShippingAddressRequest("이순신", "01022223333", zipCode, "경기도 부천시 원미구", "2층");

            // when
            MvcTestResult result = putJson(SHIPPING_ADDRESSES_API + "/" + "shipping-address-id", request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void mainAddress_속성이_지정되지_않으면_400_Bad_Request를_반환한다(String mainAddress) throws JsonProcessingException {
            // given
            var request = new UpdateShippingAddressRequest("이순신", "01022223333", "13579", mainAddress, "2층");

            // when
            MvcTestResult result = putJson(SHIPPING_ADDRESSES_API + "/" + "shipping-address-id", request);

            // then
            assertThat(result).apply(print()).hasStatus(BAD_REQUEST);
        }
    }
}
