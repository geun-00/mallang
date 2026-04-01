package io.mallang.product.adapter.web;

import io.mallang.member.adapter.security.CustomUserDetails;
import io.mallang.product.adapter.web.model.AddProductImagesRequest;
import io.mallang.product.application.provided.command.AddProductImagesUseCase;
import io.mallang.product.application.provided.command.ChangeThumbnailImageUseCase;
import io.mallang.product.application.provided.command.RemoveProductImageUseCase;
import io.mallang.product.application.provided.command.model.AddProductImagesCommand;
import io.mallang.product.application.provided.command.model.ChangeThumbnailImageCommand;
import io.mallang.product.application.provided.command.model.RemoveProductImageCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductImageCommandApi {

    private final AddProductImagesUseCase addProductImagesUseCase;
    private final RemoveProductImageUseCase removeProductImageUseCase;
    private final ChangeThumbnailImageUseCase changeThumbnailImageUseCase;

    @PostMapping("/{productId}/images")
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

    @DeleteMapping("/{productId}/images/{imageId}")
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

    @PatchMapping("/{productId}/images/{imageId}/thumbnail")
    public ResponseEntity<Void> changeThumbnail(
            @PathVariable String productId,
            @PathVariable String imageId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        changeThumbnailImageUseCase.changeThumbnail(
                new ChangeThumbnailImageCommand(
                        userDetails.getMemberIdValue(),
                        productId,
                        imageId
                )
        );

        return ResponseEntity.noContent().build();
    }
}
