package io.mallang.product.adapter.web;

import io.mallang.application.shared.query.SliceResult;
import io.mallang.product.adapter.web.model.ProductDetailResponse;
import io.mallang.product.adapter.web.model.SearchProductsRequest;
import io.mallang.product.adapter.web.model.SearchProductsResponse;
import io.mallang.product.application.provided.query.GetProductDetailUseCase;
import io.mallang.product.application.provided.query.SearchProductsUseCase;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductQueryApi {

    private final SearchProductsUseCase searchProductsUseCase;
    private final GetProductDetailUseCase getProductDetailUseCase;

    @GetMapping
    public ResponseEntity<SearchProductsResponse> search(@Valid @ModelAttribute SearchProductsRequest request) {
        SearchProductsQuery query = new SearchProductsQuery(
                request.sellerNickname(),
                request.productName(),
                request.minPrice(),
                request.maxPrice(),
                request.category(),
                request.lastProductId(),
                request.sizeOrDefault()
        );

        SliceResult<ProductListView> result = searchProductsUseCase.search(query);

        return ResponseEntity.ok(SearchProductsResponse.from(result));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> get(@PathVariable String productId) {
        return ResponseEntity.ok(ProductDetailResponse.from(getProductDetailUseCase.get(productId)));
    }
}
