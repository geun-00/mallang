package io.mallang.fixtures.api;

import io.mallang.stock.adapter.web.model.AddStockRequest;
import io.mallang.stock.adapter.web.model.DeductStockRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

public final class StockApiFixture extends ApiFixture {

    public StockApiFixture(FixtureContext context) {
        super(context);
    }

    public ResponseEntity<Void> addStock(String productId, AddStockRequest request) {
        return client().exchange(
                RequestEntity.post(STOCKS_API + "/" + productId + "/add").body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> deductStock(String productId, DeductStockRequest request) {
        return client().exchange(
                RequestEntity.post(STOCKS_API + "/" + productId + "/deduct").body(request),
                Void.class
        );
    }
}
