package io.mallang.cart.application.service.command;

import io.mallang.cart.application.provided.command.AddCartItemUseCase;
import io.mallang.cart.application.provided.command.ChangeCartItemQuantityUseCase;
import io.mallang.cart.application.provided.command.model.AddItemToCartCommand;
import io.mallang.cart.application.provided.command.model.AddItemToCartResult;
import io.mallang.cart.application.provided.command.model.ChangeCartItemQuantityCommand;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.AddCartItemCommand;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItemId;
import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartCommandService implements AddCartItemUseCase, ChangeCartItemQuantityUseCase {

    private final IdGenerator idGenerator;
    private final LoadCartPort loadCartPort;
    private final SaveCartPort saveCartPort;
    private final LoadProductPort loadProductPort;

    @Override
    public AddItemToCartResult addItem(AddItemToCartCommand command) {
        Cart cart = loadCartPort.getByMemberId(new MemberId(command.memberIdValue()));
        Product product = loadProductPort.getById(new ProductId(command.productIdValue()));

        int totalQuantity = cart.getQuantityOf(product.getId()) + command.quantity();
        product.validateEnoughStock(totalQuantity);

        CartItemId cartItemId = cart.addItem(
                new AddCartItemCommand(
                        command.productIdValue(),
                        command.quantity()
                ),
                idGenerator
        );

        saveCartPort.save(cart);

        return new AddItemToCartResult(cartItemId.value());
    }

    @Override
    public void changeQuantity(ChangeCartItemQuantityCommand command) {
        Cart cart = loadCartPort.getByMemberId(new MemberId(command.memberIdValue()));
        CartItemId cartItemId = new CartItemId(command.cartItemIdValue());

        ProductId productId = cart.getProductIdOf(cartItemId);
        Product product = loadProductPort.getById(productId);
        product.validateEnoughStock(command.quantity());

        cart.changeQuantity(cartItemId, command.quantity());

        saveCartPort.save(cart);
    }
}
