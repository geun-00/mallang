package io.mallang.product.application.service.command;

import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.AddProductImagesUseCase;
import io.mallang.product.application.provided.command.ChangeThumbnailImageUseCase;
import io.mallang.product.application.provided.command.RemoveProductImageUseCase;
import io.mallang.product.application.provided.command.model.AddProductImagesCommand;
import io.mallang.product.application.provided.command.model.ChangeThumbnailImageCommand;
import io.mallang.product.application.provided.command.model.RemoveProductImageCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.ProductImageId;
import io.mallang.product.domain.command.AddProductImageCommand;
import io.mallang.product.domain.exception.NotProductSellerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageCommandService implements AddProductImagesUseCase, RemoveProductImageUseCase, ChangeThumbnailImageUseCase {

    private final IdGenerator idGenerator;
    private final LoadProductPort loadProductPort;
    private final SaveProductPort saveProductPort;

    @Override
    public void addImages(AddProductImagesCommand command) {
        Product product = loadProductWithImagesAndValidateSeller(command.productIdValue(), command.memberIdValue());

        List<AddProductImageCommand> addCommands = command.imageUrls()
                                                          .stream()
                                                          .map(AddProductImageCommand::new)
                                                          .toList();
        product.addImages(addCommands, idGenerator);

        saveProductPort.save(product);
    }

    @Override
    public void removeImage(RemoveProductImageCommand command) {
        Product product = loadProductWithImagesAndValidateSeller(command.productIdValue(), command.memberIdValue());

        product.removeImage(new ProductImageId(command.productImageIdValue()));

        saveProductPort.save(product);
    }

    @Override
    public void changeThumbnail(ChangeThumbnailImageCommand command) {
        Product product = loadProductWithImagesAndValidateSeller(command.productIdValue(), command.memberIdValue());

        product.changeThumbnailImage(new ProductImageId(command.productImageIdValue()));

        saveProductPort.save(product);
    }

    private Product loadProductWithImagesAndValidateSeller(String productIdValue, String memberIdValue) {
        Product product = loadProductPort.getByIdWithImages(new ProductId(productIdValue));

        if (!product.isSeller(new MemberId(memberIdValue))) {
            throw new NotProductSellerException();
        }

        return product;
    }
}
