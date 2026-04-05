package io.mallang.cart.adapter.web;

import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.adapter.web.model.ChangeCartItemQuantityRequest;
import io.mallang.cart.application.provided.command.AddCartItemUseCase;
import io.mallang.cart.application.provided.command.ChangeCartItemQuantityUseCase;
import io.mallang.cart.application.provided.command.ClearCartUseCase;
import io.mallang.cart.application.provided.command.RemoveCartItemUseCase;
import io.mallang.cart.application.provided.command.model.*;
import io.mallang.member.adapter.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my/cart/items")
public class CartCommandApi {

    private final ClearCartUseCase clearCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ChangeCartItemQuantityUseCase changeCartItemQuantityUseCase;

    @PostMapping
    public ResponseEntity<Void> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AddItemToCartResult result = addCartItemUseCase.addItem(
                new AddItemToCartCommand(
                        userDetails.getMemberIdValue(),
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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        changeCartItemQuantityUseCase.changeQuantity(
                new ChangeCartItemQuantityCommand(
                        userDetails.getMemberIdValue(),
                        cartItemId,
                        request.quantity()
                )
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable String cartItemId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        removeCartItemUseCase.removeItem(
                new RemoveCartItemCommand(
                        userDetails.getMemberIdValue(),
                        cartItemId
                )
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        clearCartUseCase.clear(
                new ClearCartCommand(userDetails.getMemberIdValue())
        );

        return ResponseEntity.noContent().build();
    }
}
