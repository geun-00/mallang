package io.mallang.product.application.service.query;

import io.mallang.application.shared.query.SliceResult;
import io.mallang.product.application.provided.query.SearchProductsUseCase;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;
import io.mallang.product.application.required.query.SearchProductsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductQueryService implements SearchProductsUseCase {

    private final SearchProductsPort searchProductsPort;

    @Override
    public SliceResult<ProductListView> search(SearchProductsQuery query) {
        return searchProductsPort.search(query);
    }
}
