package io.mallang.test.product.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mallang.product.adapter.web.ProductImageCommandApi;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import io.mallang.product.application.provided.command.AddProductImagesUseCase;
import io.mallang.product.application.provided.command.ChangeThumbnailImageUseCase;
import io.mallang.product.application.provided.command.RemoveProductImageUseCase;
import io.mallang.security.config.WebMvcConfig;
import io.mallang.test.support.security.WithMockMember;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.ArrayList;

import static io.mallang.fixtures.ProductFixture.generateProductImageUrl;
import static io.mallang.fixtures.api.ApiFixture.PRODUCTS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProductImageCommandApi.class)
@Import(WebMvcConfig.class)
class ProductImageCommandApiWebMvcTest {

    @MockitoBean
    AddProductImagesUseCase addProductImagesUseCase;

    @MockitoBean
    RemoveProductImageUseCase removeProductImageUseCase;

    @MockitoBean
    ChangeThumbnailImageUseCase changeThumbnailImageUseCase;

    @Nested
    class 상품_이미지_추가_요청_검증 {

        @WithMockMember
        @Test
        void imageUrls_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다(
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {
            // given
            var request = new AddProductImagesRequest(null);

            // when
            MvcTestResult result = client.post()
                                         .uri(PRODUCTS_API + "/" + "product-id" + "/images")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "   "})
        void imageUrls_요소가_null_또는_비어있는_문자열이면_400_Bad_Request_상태코드를_반환한다(
                String invalidImageUrl,
                @Autowired MockMvcTester client,
                @Autowired ObjectMapper objectMapper
        ) throws JsonProcessingException {
            // given
            var imageUrls = new ArrayList<String>();
            imageUrls.add(generateProductImageUrl());
            imageUrls.add(invalidImageUrl);

            var request = new AddProductImagesRequest(imageUrls);

            // when
            MvcTestResult result = client.post()
                                         .uri(PRODUCTS_API + "/" + "product-id" + "/images")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .content(objectMapper.writeValueAsString(request))
                                         .exchange();

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
