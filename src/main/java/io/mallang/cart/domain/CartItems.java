package io.mallang.cart.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.product.domain.ProductId;

import java.util.ArrayList;
import java.util.List;

public class CartItems {

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
        itemIds.forEach(this::remove);
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

    List<CartItem> toList() {
        return List.copyOf(items);
    }

    private CartItem findById(CartItemId itemId) {
        return items.stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니 항목입니다."));
    }
}
