package io.mallang.cart.domain;

import io.mallang.cart.domain.exception.CartItemNotFoundException;
import io.mallang.domain.common.IdGenerator;
import io.mallang.product.domain.ProductId;

import java.util.ArrayList;
import java.util.List;

class CartItems {

    private final List<CartItem> items;

    CartItems() {
        this.items = new ArrayList<>();
    }

    private CartItems(List<CartItem> items) {
        this.items = new ArrayList<>(items);
    }

    static CartItems restore(List<CartItem> items) {
        return new CartItems(items);
    }

    CartItemId add(ProductId productId, int quantity, IdGenerator idGenerator) {
        return items.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .map(item -> {
                        item.addQuantity(quantity);
                        return item.getId();
                    })
                    .orElseGet(() -> {
                        CartItem newItem = CartItem.create(productId, quantity, idGenerator);
                        items.add(newItem);
                        return newItem.getId();
                    });
    }

    void changeQuantity(CartItemId itemId, int quantity) {
        findById(itemId).changeQuantity(quantity);
    }

    void remove(CartItemId itemId) {
        items.remove(findById(itemId));
    }

    void removeAll(List<CartItemId> itemIds) {
        List<CartItem> itemsToRemove = itemIds.stream()
                                              .map(this::findById)
                                              .toList();
        items.removeAll(itemsToRemove);
    }

    void clear() {
        items.clear();
    }

    int getQuantityOf(ProductId productId) {
        return items.stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .mapToInt(CartItem::getQuantity)
                    .sum();
    }

    List<ProductId> getProductIds() {
        return items.stream()
                    .map(CartItem::getProductId)
                    .toList();
    }

    ProductId getProductIdOf(CartItemId cartItemId) {
        return findById(cartItemId).getProductId();
    }

    List<CartItem> toList() {
        return List.copyOf(items);
    }

    private CartItem findById(CartItemId cartItemId) {
        return items.stream()
                    .filter(item -> item.getId().equals(cartItemId))
                    .findFirst()
                    .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
    }
}
