package io.mallang.cart.domain;

import io.mallang.domain.common.IdGenerator;
import io.mallang.cart.domain.command.RestoreCartItemCommand;
import io.mallang.domain.common.exception.InvalidValueException;
import io.mallang.product.domain.ProductId;
import lombok.Getter;

@Getter
public class CartItem {

    private final CartItemId id;

    private final ProductId productId;

    private int quantity;

    private CartItem(CartItemId id, ProductId productId, int quantity) {
        validateQuantity(quantity);

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    static CartItem create(ProductId productId, int quantity, IdGenerator idGenerator) {
        return new CartItem(
                new CartItemId(idGenerator.nextId()),
                productId,
                quantity
        );
    }

    public static CartItem restore(RestoreCartItemCommand command) {
        return new CartItem(
                command.id(),
                command.productId(),
                command.quantity()
        );
    }

    void addQuantity(int quantity) {
        validateQuantity(quantity);

        this.quantity += quantity;
    }

    void changeQuantity(int quantity) {
        validateQuantity(quantity);

        this.quantity = quantity;
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidValueException("수량은 1개 이상이어야 합니다.");
        }
    }
}
