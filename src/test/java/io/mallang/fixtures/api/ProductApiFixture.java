package io.mallang.fixtures.api;

import io.mallang.product.adapter.web.model.*;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static io.mallang.fixtures.ProductFixture.generateCreateProductRequest;

public final class ProductApiFixture extends ApiFixture {

    public ProductApiFixture(FixtureContext context) {
        super(context);
    }

    public ResponseEntity<Void> registerProduct(CreateProductRequest request) {
        return client().postForEntity("/products", request, Void.class);
    }

    public String registerProductThenGetId() {
        ResponseEntity<Void> response = registerProduct(generateCreateProductRequest());

        return extractId(response);
    }

    public ResponseEntity<Void> updateProduct(String productId, UpdateProductRequest request) {
        return client().exchange(
                RequestEntity.put("/products/" + productId).body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> addStock(String productId, AddStockRequest request) {
        return client().exchange(
                RequestEntity.patch("/products/" + productId + "/stock/add").body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> deductStock(String productId, DeductStockRequest request) {
        return client().exchange(
                RequestEntity.patch("/products/" + productId + "/stock/deduct").body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> discontinue(String productId) {
        return client().exchange(
                RequestEntity.patch("/products/" + productId + "/discontinue").build(),
                Void.class
        );
    }

    public ResponseEntity<Void> addImages(String productId, AddProductImagesRequest request) {
        return client().exchange(
                RequestEntity.post("/products/" + productId + "/images").body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> removeImage(String productId, String imageId) {
        return client().exchange(
                RequestEntity.delete("/products/" + productId + "/images/" + imageId).build(),
                Void.class
        );
    }

    public ResponseEntity<Void> changeThumbnailImage(String productId, String imageId) {
        return client().exchange(
                RequestEntity.patch("/products/" + productId + "/images/" + imageId + "/thumbnail").build(),
                Void.class
        );
    }
}
