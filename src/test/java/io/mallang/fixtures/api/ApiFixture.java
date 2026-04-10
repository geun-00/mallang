package io.mallang.fixtures.api;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

public abstract class ApiFixture {

    private static final String API_PREFIX = "/api/v1";

    public static final String MEMBERS_API = API_PREFIX + "/members";
    public static final String SHIPPING_ADDRESSES_API = API_PREFIX + "/my/shipping-addresses";
    public static final String CART_ITEMS_API = API_PREFIX + "/my/cart/items";
    public static final String ORDERS_API = API_PREFIX + "/my/orders";
    public static final String PRODUCTS_API = API_PREFIX + "/products";

    private final FixtureContext context;

    protected ApiFixture(FixtureContext context) {
        this.context = context;
    }

    protected TestRestTemplate authenticatedClient() {
        return context.authenticatedClient();
    }

    protected FixtureContext context() {
        return context;
    }

    protected String extractId(ResponseEntity<Void> response) {
        String path = response.getHeaders().getLocation().getPath();

        return path.substring(path.lastIndexOf('/') + 1);
    }

    public TestRestTemplate client() {
        return authenticatedClient();
    }

    public TestRestTemplate unauthenticatedClient() {
        return context.unauthenticatedClient();
    }
}
