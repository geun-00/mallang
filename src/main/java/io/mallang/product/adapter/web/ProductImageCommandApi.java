package io.mallang.product.adapter.web;

import io.mallang.member.adapter.security.CustomUserDetails;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import io.mallang.product.application.provided.command.AddProductImagesUseCase;
import io.mallang.product.application.provided.command.model.AddProductImagesCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductImageCommandApi {

    private final AddProductImagesUseCase addProductImagesUseCase;

    @PostMapping("/products/{productId}/images")
    public ResponseEntity<Void> addImages(
            @PathVariable String productId,
            @Valid @RequestBody AddProductImagesRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        addProductImagesUseCase.addImages(
                new AddProductImagesCommand(
                        userDetails.getMemberIdValue(),
                        productId,
                        request.imageUrls()
                )
        );

        return ResponseEntity.noContent().build();
    }
}
