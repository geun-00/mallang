package io.mallang.test.product.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.product.adapter.web.ProductImageCommandApi;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import io.mallang.product.application.provided.command.AddProductImagesUseCase;
import io.mallang.product.application.provided.command.ChangeThumbnailImageUseCase;
import io.mallang.product.application.provided.command.RemoveProductImageUseCase;
import io.mallang.test.support.security.WithMockMember;
import io.mallang.test.support.web.WebMvcRequestTestSupport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.ArrayList;

import static io.mallang.fixtures.ProductFixture.generateProductImageUrl;
import static io.mallang.fixtures.api.ApiFixture.PRODUCTS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@WebMvcAdapterTest(ProductImageCommandApi.class)
class ProductImageCommandApiWebMvcTest extends WebMvcRequestTestSupport {

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
        void imageUrls_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new AddProductImagesRequest(null);

            // when
            MvcTestResult result = postJson(PRODUCTS_API + "/" + "product-id" + "/images", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "   "})
        void imageUrls_요소가_null_또는_비어있는_문자열이면_400_Bad_Request_상태코드를_반환한다(String invalidImageUrl) throws JsonProcessingException {
            // given
            var imageUrls = new ArrayList<String>();
            imageUrls.add(generateProductImageUrl());
            imageUrls.add(invalidImageUrl);

            var request = new AddProductImagesRequest(imageUrls);

            // when
            MvcTestResult result = postJson(PRODUCTS_API + "/" + "product-id" + "/images", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
