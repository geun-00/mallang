package io.mallang.fixtures.api;

import io.mallang.product.adapter.web.model.*;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static io.mallang.fixtures.ProductFixture.generateCreateProductRequest;

public final class ProductApiFixture extends ApiFixture {

    public ProductApiFixture(FixtureContext context) {
        super(context);
    }

    public ResponseEntity<Void> registerProduct(CreateProductRequest request) {
        return client().postForEntity(PRODUCTS_API, request, Void.class);
    }

    public String registerProductThenGetId() {
        ResponseEntity<Void> response = registerProduct(generateCreateProductRequest());

        return extractId(response);
    }

    public ResponseEntity<Void> updateProduct(String productId, UpdateProductRequest request) {
        return client().exchange(
                RequestEntity.put(PRODUCTS_API + "/" + productId).body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> discontinue(String productId) {
        return client().exchange(
                RequestEntity.patch(PRODUCTS_API + "/" + productId + "/discontinue").build(),
                Void.class
        );
    }

    public ResponseEntity<Void> addImages(String productId, AddProductImagesRequest request) {
        return client().exchange(
                RequestEntity.post(PRODUCTS_API + "/" + productId + "/images").body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> removeImage(String productId, String imageId) {
        return client().exchange(
                RequestEntity.delete(PRODUCTS_API + "/" + productId + "/images/" + imageId).build(),
                Void.class
        );
    }

    public ResponseEntity<Void> changeThumbnailImage(String productId, String imageId) {
        return client().exchange(
                RequestEntity.patch(PRODUCTS_API + "/" + productId + "/images/" + imageId + "/thumbnail").build(),
                Void.class
        );
    }

    public ResponseEntity<SearchProductsResponse> searchProducts(SearchProductsRequest request, String lastProductId, Integer size) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(PRODUCTS_API);
        addIfPresent(builder, "sellerNickname", request.sellerNickname());
        addIfPresent(builder, "productName", request.productName());
        addIfPresent(builder, "minPrice", request.minPrice());
        addIfPresent(builder, "maxPrice", request.maxPrice());
        addIfPresent(builder, "category", request.category());
        addIfPresent(builder, "lastProductId", lastProductId);
        addIfPresent(builder, "size", size);

        return client().getForEntity(builder.toUriString(), SearchProductsResponse.class);
    }

    private void addIfPresent(UriComponentsBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value);
        }
    }

    public ResponseEntity<ProductDetailResponse> getProductDetail(String productId) {
        return client().getForEntity(PRODUCTS_API + "/" + productId, ProductDetailResponse.class);
    }
}
