package io.mallang.product.application.provided.query;

import io.mallang.common.applicaiton.query.SliceResult;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;

public interface SearchProductsUseCase {

    SliceResult<ProductListView> search(SearchProductsQuery query);
}
