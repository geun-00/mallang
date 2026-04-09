package io.mallang.product.adapter.web;

import io.mallang.common.applicaiton.query.SliceResult;
import io.mallang.product.adapter.web.model.ProductDetailResponse;
import io.mallang.product.adapter.web.model.SearchProductsRequest;
import io.mallang.product.adapter.web.model.SearchProductsResponse;
import io.mallang.product.application.provided.query.GetProductDetailUseCase;
import io.mallang.product.application.provided.query.SearchProductsUseCase;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.mallang.product.adapter.web.mapper.ProductResponseMapper.toProductDetailResponse;
import static io.mallang.product.adapter.web.mapper.ProductResponseMapper.toSearchProductsResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductQueryApi {

    private final SearchProductsUseCase searchProductsUseCase;
    private final GetProductDetailUseCase getProductDetailUseCase;

    @GetMapping
    public ResponseEntity<SearchProductsResponse> search(
            @Valid @ModelAttribute SearchProductsRequest request,
            @RequestParam(required = false) String lastProductId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        SearchProductsQuery query = new SearchProductsQuery(
                request.sellerNickname(),
                request.productName(),
                request.minPrice(),
                request.maxPrice(),
                request.category(),
                lastProductId,
                pageable.getPageSize()
        );

        SliceResult<ProductListView> result = searchProductsUseCase.search(query);

        return ResponseEntity.ok(toSearchProductsResponse(result));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> get(@PathVariable String productId) {
        return ResponseEntity.ok(toProductDetailResponse(getProductDetailUseCase.get(productId)));
    }
}
