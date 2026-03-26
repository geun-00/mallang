package io.mallang.product.adapter.web;

import io.mallang.member.adapter.security.CustomUserDetails;
import io.mallang.product.adapter.web.model.AddStockRequest;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.adapter.web.model.DeductStockRequest;
import io.mallang.product.adapter.web.model.UpdateProductRequest;
import io.mallang.product.application.provided.command.AddStockUseCase;
import io.mallang.product.application.provided.command.DeductStockUseCase;
import io.mallang.product.application.provided.command.RegisterProductUseCase;
import io.mallang.product.application.provided.command.UpdateProductUseCase;
import io.mallang.product.application.provided.command.model.AddStockCommand;
import io.mallang.product.application.provided.command.model.DeductStockCommand;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductImageCommand;
import io.mallang.product.application.provided.command.model.RegisterProductResult;
import io.mallang.product.application.provided.command.model.UpdateProductCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductCommandApi {

    private final RegisterProductUseCase registerProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final AddStockUseCase addStockUseCase;
    private final DeductStockUseCase deductStockUseCase;

    @PostMapping("/products")
    public ResponseEntity<Void> register(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<RegisterProductImageCommand> imageCommands =
                request.images() == null
                        ? List.of()
                        : request.images().stream()
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

        return ResponseEntity.created(URI.create("/products/" + result.productId())).build();
    }

    @PutMapping("/products/{productId}")
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

    @PatchMapping("/products/{productId}/stock/add")
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

    @PatchMapping("/products/{productId}/stock/deduct")
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
}
