package io.mallang.product.adapter.web;

import io.mallang.member.adapter.security.CustomUserDetails;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import io.mallang.product.application.provided.command.AddProductImagesUseCase;
import io.mallang.product.application.provided.command.RemoveProductImageUseCase;
import io.mallang.product.application.provided.command.model.AddProductImagesCommand;
import io.mallang.product.application.provided.command.model.RemoveProductImageCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductImageCommandApi {

    private final AddProductImagesUseCase addProductImagesUseCase;
    private final RemoveProductImageUseCase removeProductImageUseCase;

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

    @DeleteMapping("/products/{productId}/images/{imageId}")
    public ResponseEntity<Void> removeImage(
            @PathVariable String productId,
            @PathVariable String imageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        removeProductImageUseCase.removeImage(
                new RemoveProductImageCommand(
                        userDetails.getMemberIdValue(),
                        productId,
                        imageId
                )
        );

        return ResponseEntity.noContent().build();
    }
}
