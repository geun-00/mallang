package io.mallang.product.adapter.web;

import io.mallang.common.adapter.web.auth.CurrentMemberId;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import io.mallang.product.application.provided.command.*;
import io.mallang.product.application.provided.command.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductCommandApi {

    private final UpdateProductUseCase updateProductUseCase;
    private final RegisterProductUseCase registerProductUseCase;
    private final DiscontinueProductUseCase discontinueProductUseCase;

    @PostMapping
    public ResponseEntity<Void> register(
            @Valid @RequestBody CreateProductRequest request,
            @CurrentMemberId String memberId
    ) {
        List<RegisterProductImageCommand> imageCommands =
                request.images() == null
                        ? List.of()
                        : request.images()
                                 .stream()
                                 .map(image -> new RegisterProductImageCommand(image.imageUrl(), image.isThumbnail()))
                                 .toList();

        RegisterProductResult result = registerProductUseCase.register(
                new RegisterProductCommand(
                        memberId,
                        request.name(),
                        request.description(),
                        request.price(),
                        request.stockQuantity(),
                        request.category(),
                        imageCommands
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(result.productId())
                                                  .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> update(
            @PathVariable String productId,
            @Valid @RequestBody UpdateProductRequest request,
            @CurrentMemberId String memberId
    ) {
        updateProductUseCase.update(
                new UpdateProductCommand(
                        memberId,
                        productId,
                        request.name(),
                        request.description(),
                        request.price(),
                        request.category()
                )
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/discontinue")
    public ResponseEntity<Void> discontinue(
            @PathVariable String productId,
            @CurrentMemberId String memberId
    ) {
        discontinueProductUseCase.discontinue(
                new DiscontinueProductCommand(
                        memberId,
                        productId
                )
        );

        return ResponseEntity.noContent().build();
    }
}
