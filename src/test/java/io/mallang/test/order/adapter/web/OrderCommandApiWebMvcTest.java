package io.mallang.test.order.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.order.adapter.web.OrderCommandApi;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.provided.command.CancelOrderUseCase;
import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.test.support.security.WithMockMember;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.ArrayList;
import java.util.List;

import static io.mallang.fixtures.api.ApiFixture.ORDERS_API;
import static io.mallang.order.adapter.web.model.CreateOrderRequest.CreateOrderItemRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@WebMvcAdapterTest(OrderCommandApi.class)
class OrderCommandApiWebMvcTest {

    @MockitoBean
    CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    CancelOrderUseCase cancelOrderUseCase;

    @Nested
    class 주문_생성_요청_검증 {

        @WithMockMember
        @Test
        void items_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(null, "홍길동", "01012345678", "12345", "서울시 강남구 테헤란로 1", "101호");

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void items_요소가_null이면_400_Bad_Request_상태코드를_반환한다(
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            List<CreateOrderItemRequest> items = new ArrayList<>();
            items.add(new CreateOrderItemRequest("product-id", 1));
            items.add(null);

            var request = new CreateOrderRequest(items, "홍길동", "01012345678", "12345", "서울시 강남구 테헤란로 1", "101호");

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " "})
        void item_productId가_null_또는_비어있으면_400_Bad_Request_상태코드를_반환한다(
                String invalidProductId,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(
                    List.of(new CreateOrderItemRequest(invalidProductId, 1)),
                    "홍길동",
                    "01012345678",
                    "12345",
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void item_quantity가_null이면_400_Bad_Request_상태코드를_반환한다(
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(
                    List.of(new CreateOrderItemRequest("product-id", null)),
                    "홍길동",
                    "01012345678",
                    "12345",
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void item_quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
                int invalidQuantity,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(
                    List.of(new CreateOrderItemRequest("product-id", invalidQuantity)),
                    "홍길동",
                    "01012345678",
                    "12345",
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void receiverName_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidReceiverName,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(
                    List.of(new CreateOrderItemRequest("product-id", 1)),
                    invalidReceiverName,
                    "01012345678",
                    "12345",
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void receiverPhoneNumber_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidReceiverPhoneNumber,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(
                    List.of(new CreateOrderItemRequest("product-id", 1)),
                    "홍길동",
                    invalidReceiverPhoneNumber,
                    "12345",
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void zipCode_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidZipCode,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(
                    List.of(new CreateOrderItemRequest("product-id", 1)),
                    "홍길동",
                    "01012345678",
                    invalidZipCode,
                    "서울시 강남구 테헤란로 1",
                    "101호"
            );

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" "})
        void mainAddress_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidMainAddress,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {

            // given
            var request = new CreateOrderRequest(
                    List.of(new CreateOrderItemRequest("product-id", 1)),
                    "홍길동",
                    "01012345678",
                    "12345",
                    invalidMainAddress,
                    "101호"
            );

            // when
            MvcTestResult result = client.post()
                                         .uri(ORDERS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
