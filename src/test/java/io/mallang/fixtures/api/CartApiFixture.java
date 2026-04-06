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
        return client().postForEntity("/my/cart/items", request, Void.class);
    }

    public String addCartItemThenGetId(String productId, int quantity) {
        ResponseEntity<Void> response = addCartItem(new AddCartItemRequest(productId, quantity));
        return response.getHeaders().getLocation().getPath().substring("/my/cart/items/".length());
    }

    public ResponseEntity<Void> changeCartItemQuantity(String cartItemId, ChangeCartItemQuantityRequest request) {
        return client().exchange(
                RequestEntity.patch("/my/cart/items/" + cartItemId).body(request),
                Void.class
        );
    }

    public ResponseEntity<Void> removeCartItem(String cartItemId) {
        return client().exchange(
                RequestEntity.delete("/my/cart/items/" + cartItemId).build(),
                Void.class
        );
    }

    public ResponseEntity<Void> clearCart() {
        return client().exchange(
                RequestEntity.delete("/my/cart/items").build(),
                Void.class
        );
    }
}
