package io.mallang.product.adapter.web;

import io.mallang.common.adapter.security.CustomUserDetails;
import io.mallang.product.adapter.web.model.AddStockRequest;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.adapter.web.model.DeductStockRequest;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import io.mallang.product.application.provided.command.*;
import io.mallang.product.application.provided.command.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductCommandApi {

    private final AddStockUseCase addStockUseCase;
    private final DeductStockUseCase deductStockUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final RegisterProductUseCase registerProductUseCase;
    private final DiscontinueProductUseCase discontinueProductUseCase;

    @PostMapping
    public ResponseEntity<Void> register(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
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
                        userDetails.getMemberIdValue(),
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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        updateProductUseCase.update(
                new UpdateProductCommand(
                        userDetails.getMemberIdValue(),
                        productId,
                        request.name(),
                        request.description(),
                        request.price(),
                        request.category()
                )
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/stock/add")
    public ResponseEntity<Void> addStock(
            @PathVariable String productId,
            @Valid @RequestBody AddStockRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        addStockUseCase.addStock(
                new AddStockCommand(
                        userDetails.getMemberIdValue(),
                        productId,
                        request.quantity()
                )
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/stock/deduct")
    public ResponseEntity<Void> deductStock(
            @PathVariable String productId,
            @Valid @RequestBody DeductStockRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        deductStockUseCase.deductStock(
                new DeductStockCommand(
                        userDetails.getMemberIdValue(),
                        productId,
                        request.quantity()
                )
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/discontinue")
    public ResponseEntity<Void> discontinue(
            @PathVariable String productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        discontinueProductUseCase.discontinue(
                new DiscontinueProductCommand(
                        userDetails.getMemberIdValue(),
                        productId
                )
        );

        return ResponseEntity.noContent().build();
    }
}
