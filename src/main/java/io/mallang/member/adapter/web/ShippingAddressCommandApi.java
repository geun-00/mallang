package io.mallang.member.adapter.web;

import io.mallang.common.adapter.web.auth.CurrentMemberId;
import io.mallang.member.adapter.web.model.RegisterShippingAddressRequest;
import io.mallang.member.adapter.web.model.UpdateShippingAddressRequest;
import io.mallang.member.application.provided.command.RegisterShippingAddressUseCase;
import io.mallang.member.application.provided.command.RemoveShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateDefaultShippingAddressUseCase;
import io.mallang.member.application.provided.command.UpdateShippingAddressUseCase;
import io.mallang.member.application.provided.command.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my/shipping-addresses")
public class ShippingAddressCommandApi {

    private final UpdateShippingAddressUseCase updateShippingAddressUseCase;
    private final RemoveShippingAddressUseCase removeShippingAddressUseCase;
    private final RegisterShippingAddressUseCase registerShippingAddressUseCase;
    private final UpdateDefaultShippingAddressUseCase updateDefaultShippingAddressUseCase;

    @PostMapping
    public ResponseEntity<Void> registerShippingAddress(
            @Valid @RequestBody RegisterShippingAddressRequest request,
            @CurrentMemberId String memberId
    ) {
        RegisterShippingAddressResult result = registerShippingAddressUseCase.register(
                new RegisterShippingAddressCommand(
                        memberId,
                        request.receiverName(),
                        request.receiverPhoneNumber(),
                        request.zipCode(),
                        request.mainAddress(),
                        request.detailAddress()
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(result.shippingAddressId())
                                                  .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{shippingAddressId}/default")
    public ResponseEntity<Void> updateDefault(
            @PathVariable String shippingAddressId,
            @CurrentMemberId String memberId
    ) {
        updateDefaultShippingAddressUseCase.update(
                new UpdateDefaultShippingAddressCommand(
                        memberId,
                        shippingAddressId
                ));

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{shippingAddressId}")
    public ResponseEntity<Void> updateShippingAddress(
            @PathVariable String shippingAddressId,
            @Valid @RequestBody UpdateShippingAddressRequest request,
            @CurrentMemberId String memberId
    ) {
        updateShippingAddressUseCase.update(
                new UpdateShippingAddressCommand(
                        memberId,
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

    @DeleteMapping("/{shippingAddressId}")
    public ResponseEntity<Void> removeShippingAddress(
            @PathVariable String shippingAddressId,
            @CurrentMemberId String memberId
    ) {
        removeShippingAddressUseCase.remove(new RemoveShippingAddressCommand(
                memberId,
                shippingAddressId
        ));

        return ResponseEntity.noContent().build();
    }
}
