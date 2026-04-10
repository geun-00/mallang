package io.mallang.fixtures.api;

import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

public final class CartApiFixture extends ApiFixture {

    public CartApiFixture(FixtureContext context) {
        super(context);
    }

    public ResponseEntity<Void> addCartItem(AddCartItemRequest request) {
        return client().postForEntity(CART_ITEMS_API, request, Void.class);
    }

    public String addCartItemThenGetId(String productId, int quantity) {
        ResponseEntity<Void> response = addCartItem(new AddCartItemRequest(productId, quantity));
        return extractId(response);
    }

    public ResponseEntity<Void> changeCartItemQuantity(String cartItemId, ChangeCartItemQuantityRequest request) {
        return client().exchange(
                RequestEntity.patch(CART_ITEMS_API + "/" + cartItemId).body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> removeCartItem(String cartItemId) {
        return client().exchange(
                RequestEntity.delete(CART_ITEMS_API + "/" + cartItemId).build(),
                Void.class
        );
    }

    public ResponseEntity<Void> clearCart() {
        return client().exchange(
                RequestEntity.delete(CART_ITEMS_API).build(),
                Void.class
        );
    }
}
