package io.mallang.cart.adapter.web;

import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import io.mallang.cart.application.provided.command.AddCartItemUseCase;
import io.mallang.cart.application.provided.command.ChangeCartItemQuantityUseCase;
import io.mallang.cart.application.provided.command.ClearCartUseCase;
import io.mallang.cart.application.provided.command.RemoveCartItemUseCase;
import io.mallang.cart.application.provided.command.model.*;
import io.mallang.common.adapter.web.auth.CurrentMemberId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my/cart/items")
public class CartCommandApi {

    private final ClearCartUseCase clearCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase;

    @PostMapping
    public ResponseEntity<Void> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            @CurrentMemberId String memberId
    ) {
        AddItemToCartResult result = addCartItemUseCase.addItem(
                new AddItemToCartCommand(
                        memberId,
                        request.productId(),
                        request.quantity()
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(result.cartItemId())
                                                  .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<Void> changeQuantity(
            @PathVariable String cartItemId,
            @Valid @RequestBody ChangeCartItemQuantityRequest request,
            @CurrentMemberId String memberId
    ) {
        changeCartItemQuantityUseCase.changeQuantity(
                new ChangeCartItemQuantityCommand(
                        memberId,
                        cartItemId,
                        request.quantity()
                )
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable String cartItemId,
            @CurrentMemberId String memberId
    ) {
        removeCartItemUseCase.removeItem(
                new RemoveCartItemCommand(
                        memberId,
                        cartItemId
                )
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(
            @CurrentMemberId String memberId
    ) {
        clearCartUseCase.clear(
                new ClearCartCommand(memberId)
        );

        return ResponseEntity.noContent().build();
    }
}
