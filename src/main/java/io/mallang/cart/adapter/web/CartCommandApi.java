package io.mallang.cart.adapter.web;

import io.mallang.cart.adapter.web.model.AddCartItemRequest;
import io.mallang.cart.application.provided.command.AddCartItemUseCase;
import io.mallang.cart.application.provided.command.model.AddItemToCartCommand;
import io.mallang.cart.application.provided.command.model.AddItemToCartResult;
import io.mallang.member.adapter.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class CartCommandApi {

    private final AddCartItemUseCase addCartItemUseCase;

    @PostMapping("/my/cart/items")
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

        return ResponseEntity.created(URI.create("/my/cart/items/" + result.cartItemId())).build();
    }
}
