package io.mallang.cart.domain;

import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.cart.domain.command.RestoreCartCommand;
import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.ProductId;
import lombok.Getter;

import java.util.List;

@Getter
public class Cart {

    private final MemberId memberId;

    private final CartItems items;

    private Cart(MemberId memberId) {
        this.memberId = memberId;
        this.items = new CartItems();
    }

    private Cart(MemberId memberId, CartItems items) {
        this.memberId = memberId;
        this.items = items;
    }

    public static Cart create(MemberId memberId) {
        return new Cart(memberId);
    }

    public static Cart restore(RestoreCartCommand command) {
        return new Cart(command.memberId(), CartItems.restore(command.items()));
    }

    public CartItemId addItem(AddCartItemCommand command, IdGenerator idGenerator) {
        return items.add(new ProductId(command.productId()), command.quantity(), idGenerator);
    }

    public void changeQuantity(CartItemId itemId, int quantity) {
        items.changeQuantity(itemId, quantity);
    }

    public ProductId getProductIdOf(CartItemId itemId) {
        return items.getProductIdOf(itemId);
    }

    public void removeItem(CartItemId itemId) {
        items.remove(itemId);
    }

    public void removeItems(List<CartItemId> itemIds) {
        items.removeAll(itemIds);
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return items.toList();
    }

    public int getQuantityOf(ProductId productId) {
        return items.getQuantityOf(productId);
    }

    public List<ProductId> getProductIds() {
        return items.getProductIds();
    }
}
