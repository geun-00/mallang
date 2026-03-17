package io.mallang.cart.domain;

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

    public static Cart create(MemberId memberId) {
        return new Cart(memberId);
    }

    public CartItemId addItem(AddCartItemCommand command, IdGenerator idGenerator) {
        return items.add(new ProductId(command.productId()), command.quantity(), idGenerator);
    }

    public void changeQuantity(CartItemId itemId, int quantity) {
        items.changeQuantity(itemId, quantity);
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

    public List<ProductId> getProductIds() {
        return items.getProductIds();
    }
}
