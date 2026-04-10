package io.mallang.product.application.required.query;

import io.mallang.common.application.query.SliceResult;
import io.mallang.product.application.provided.query.model.ProductListView;
import io.mallang.product.application.provided.query.model.SearchProductsQuery;

public interface SearchProductsPort {

    SliceResult<ProductListView> search(SearchProductsQuery query);
}
