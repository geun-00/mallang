package io.mallang.cart.application.service.command;

import io.mallang.cart.application.provided.command.AddCartItemUseCase;
import io.mallang.cart.application.provided.command.ChangeCartItemQuantityUseCase;
import io.mallang.cart.application.provided.command.ClearCartUseCase;
import io.mallang.cart.application.provided.command.RemoveCartItemUseCase;
import io.mallang.cart.application.provided.command.model.*;
import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.application.required.query.LoadCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.cart.domain.CartItem;
import io.mallang.cart.domain.CartItemId;
import io.mallang.cart.domain.command.AddCartItemCommand;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.application.required.query.LoadStockPort;
import io.mallang.stock.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartCommandService implements AddCartItemUseCase,
                                           ChangeCartItemQuantityUseCase,
                                           RemoveCartItemUseCase,
                                           ClearCartUseCase {

    private final IdGenerator idGenerator;
    private final LoadCartPort loadCartPort;
    private final SaveCartPort saveCartPort;
    private final LoadStockPort loadStockPort;
    private final LoadProductPort loadProductPort;

    @Override
    public AddItemToCartResult addItem(AddItemToCartCommand command) {
        Cart cart = loadCart(command.memberIdValue());
        Product product = loadProduct(new ProductId(command.productIdValue()));

        Stock stock = loadStock(product);

        int totalQuantity = cart.getQuantityOf(product.getId()) + command.quantity();
        stock.checkAvailable(totalQuantity);

        CartItemId cartItemId = cart.addItem(
                new AddCartItemCommand(
                        product.getId(),
                        command.quantity()
                ),
                idGenerator
        );

        saveCartPort.save(cart);

        return new AddItemToCartResult(cartItemId.value());
    }

    @Override
    public void changeQuantity(ChangeCartItemQuantityCommand command) {
        Cart cart = loadCart(command.memberIdValue());
        CartItemId cartItemId = new CartItemId(command.cartItemIdValue());

        CartItem item = cart.getItem(cartItemId);
        Product product = loadProduct(item.getProductId());

        Stock stock = loadStock(product);

        stock.checkAvailable(command.quantity());

        cart.changeQuantity(cartItemId, command.quantity());

        saveCartPort.save(cart);
    }

    @Override
    public void removeItem(RemoveCartItemCommand command) {
        Cart cart = loadCart(command.memberIdValue());

        cart.removeItem(new CartItemId(command.cartItemIdValue()));

        saveCartPort.save(cart);
    }

    @Override
    public void clear(ClearCartCommand command) {
        Cart cart = loadCart(command.memberIdValue());

        cart.clear();

        saveCartPort.save(cart);
    }

    private Cart loadCart(String command) {
        return loadCartPort.getByMemberId(new MemberId(command));
    }

    private Product loadProduct(ProductId productId) {
        return loadProductPort.getById(productId);
    }

    private Stock loadStock(Product product) {
        return loadStockPort.getByProductId(product.getId());
    }
}
