package io.mallang.product.adapter.web;

import io.mallang.common.adapter.web.auth.CurrentMemberId;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductImageCommandApi {

    private final AddProductImagesUseCase addProductImagesUseCase;
    private final RemoveProductImageUseCase removeProductImageUseCase;
    private final ChangeThumbnailImageUseCase changeThumbnailImageUseCase;

    @PostMapping("/{productId}/images")
    public ResponseEntity<Void> addImages(
            @PathVariable String productId,
            @Valid @RequestBody AddProductImagesRequest request,
            @CurrentMemberId String memberId
    ) {
        addProductImagesUseCase.addImages(
                new AddProductImagesCommand(
                        memberId,
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
            @CurrentMemberId String memberId
    ) {
        removeProductImageUseCase.removeImage(
                new RemoveProductImageCommand(
                        memberId,
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
            @CurrentMemberId String memberId
    ) {
        changeThumbnailImageUseCase.changeThumbnail(
                new ChangeThumbnailImageCommand(
                        memberId,
                        productId,
                        imageId
                )
        );

        return ResponseEntity.noContent().build();
    }
}
