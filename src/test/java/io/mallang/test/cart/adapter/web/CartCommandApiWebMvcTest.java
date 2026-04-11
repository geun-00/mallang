package io.mallang.test.cart.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.cart.adapter.web.CartCommandApi;
import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import io.mallang.cart.application.provided.command.AddCartItemUseCase;
import io.mallang.cart.application.provided.command.ChangeCartItemQuantityUseCase;
import io.mallang.cart.application.provided.command.ClearCartUseCase;
import io.mallang.cart.application.provided.command.RemoveCartItemUseCase;
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

import static io.mallang.fixtures.api.ApiFixture.CART_ITEMS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@WebMvcAdapterTest(CartCommandApi.class)
class CartCommandApiWebMvcTest {

    @MockitoBean
    ClearCartUseCase clearCartUseCase;

    @MockitoBean
    AddCartItemUseCase addCartItemUseCase;

    @MockitoBean
    RemoveCartItemUseCase removeCartItemUseCase;

    @MockitoBean
    ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase;

    @Nested
    class 항목_추가_요청_검증 {

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " "})
        void productId_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                String invalidProductId,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {
            // given
            var request = new AddCartItemRequest(invalidProductId, 2);

            // when
            MvcTestResult result = client.post()
                                         .uri(CART_ITEMS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {
            // given
            var request = new AddCartItemRequest("product-id", null);

            // when
            MvcTestResult result = client.post()
                                         .uri(CART_ITEMS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
                int invalidQuantity,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {
            // given
            var request = new AddCartItemRequest("product-id", invalidQuantity);

            // when
            MvcTestResult result = client.post()
                                         .uri(CART_ITEMS_API)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }

    @Nested
    class 수량_변경_요청_검증 {

        @WithMockMember
        @Test
        void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {
            // given
            var request = new ChangeCartItemQuantityRequest(null);

            // when
            MvcTestResult result = client.patch()
                                         .uri(CART_ITEMS_API + "/" + "cart-item-id")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void quantity가_0_이하이면_400_Bad_Request_상태코드를_반환한다(
                int invalidQuantity,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {
            // given
            var request = new ChangeCartItemQuantityRequest(invalidQuantity);

            // when
            MvcTestResult result = client.patch()
                                         .uri(CART_ITEMS_API + "/" + "cart-item-id")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
