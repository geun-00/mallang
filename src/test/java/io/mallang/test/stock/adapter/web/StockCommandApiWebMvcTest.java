package io.mallang.test.stock.adapter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.mallang.annotations.WebMvcAdapterTest;
import io.mallang.stock.adapter.web.StockCommandApi;
import io.mallang.stock.adapter.web.model.AddStockRequest;
import io.mallang.stock.adapter.web.model.DeductStockRequest;
import io.mallang.stock.application.provided.command.AddStockUseCase;
import io.mallang.stock.application.provided.command.DeductStockUseCase;
import io.mallang.test.support.security.WithMockMember;
import io.mallang.test.support.web.WebMvcRequestTestSupport;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static io.mallang.fixtures.api.ApiFixture.STOCKS_API;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@WebMvcAdapterTest(StockCommandApi.class)
class StockCommandApiWebMvcTest extends WebMvcRequestTestSupport {

    @MockitoBean
    AddStockUseCase addStockUseCase;

    @MockitoBean
    DeductStockUseCase deductStockUseCase;

    @Nested
    class 재고_추가_요청_검증 {

        @WithMockMember
        @Test
        void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new AddStockRequest(null);

            // when
            MvcTestResult result = postJson(STOCKS_API + "/" + "product-id" + "/add", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }

        @WithMockMember
        @Test
        void quantity가_양수가_아니면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new AddStockRequest(-1);

            // when
            MvcTestResult result = postJson(STOCKS_API + "/" + "product-id" + "/add", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }

    @Nested
    class 재고_차감_요청_검증 {

        @WithMockMember
        @Test
        void quantity_속성이_지정되지_않으면_400_Bad_Request_상태코드를_반환한다() throws JsonProcessingException {
            // given
            var request = new DeductStockRequest(null);

            // when
            MvcTestResult result = postJson(STOCKS_API + "/" + "product-id" + "/deduct", request);

            // then
            assertThat(result).hasStatus(BAD_REQUEST);
        }
    }
}
