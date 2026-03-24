package io.mallang.member.adapter.web;

import io.mallang.member.adapter.security.CustomUserDetails;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import io.mallang.member.application.provided.command.RegisterShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateDefaultShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.RegisterShippingAddressCommand;
import io.mallang.member.application.provided.command.model.RegisterShippingAddressResult;
import io.mallang.member.application.provided.command.model.UpdateDefaultShippingAddressCommand;
import io.mallang.member.application.provided.command.model.UpdateShippingAddressCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class ShippingAddressCommandApi {

    private final RegisterShippingAddressUseCase registerShippingAddressUseCase;
    private final UpdateDefaultShippingAddressUseCase updateDefaultShippingAddressUseCase;
    private final UpdateShippingAddressUseCase updateShippingAddressUseCase;

    @PostMapping("/my/shipping-addresses")
    public ResponseEntity<?> registerShippingAddress(
            @Valid @RequestBody RegisterShippingAddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        RegisterShippingAddressResult result = registerShippingAddressUseCase.register(
                new RegisterShippingAddressCommand(
                        userDetails.getMemberIdValue(),
                        request.receiverName(),
                        request.receiverPhoneNumber(),
                        request.zipCode(),
                        request.mainAddress(),
                        request.detailAddress()
                )
        );

        return ResponseEntity.created(URI.create("/my/shipping-addresses/" + result.shippingAddressId())).build();
    }

    @PatchMapping("/my/shipping-addresses/{shippingAddressId}/default")
    public ResponseEntity<?> updateDefault(
            @PathVariable String shippingAddressId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        updateDefaultShippingAddressUseCase.update(
                new UpdateDefaultShippingAddressCommand(
                        userDetails.getMemberIdValue(),
                        shippingAddressId
                ));

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/my/shipping-addresses/{shippingAddressId}")
    public ResponseEntity<?> updateShippingAddress(
            @PathVariable String shippingAddressId,
            @Valid @RequestBody UpdateShippingAddressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        updateShippingAddressUseCase.update(
                new UpdateShippingAddressCommand(
                        userDetails.getMemberIdValue(),
                        shippingAddressId,
                        request.receiverName(),
                        request.receiverPhoneNumber(),
                        request.zipCode(),
                        request.mainAddress(),
                        request.detailAddress()
                )
        );

        return ResponseEntity.noContent().build();
    }
}
