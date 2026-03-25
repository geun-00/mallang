package io.mallang.product.adapter.web;

import io.mallang.member.adapter.security.CustomUserDetails;
import io.mallang.product.adapter.web.model.CreateProductRequest;
import io.mallang.product.application.provided.command.RegisterProductUseCase;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductImageCommand;
import io.mallang.product.application.provided.command.model.RegisterProductResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductCommandApi {

    private final RegisterProductUseCase registerProductUseCase;

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
}
