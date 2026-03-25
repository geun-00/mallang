package io.mallang.product.application.service.command;

import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.*;
import io.mallang.product.application.provided.command.model.*;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.command.ModifyProductCommand;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.CreateProductImageCommand;
import io.mallang.product.domain.exception.NotProductSellerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService implements RegisterProductUseCase, AddStockUseCase, DeductStockUseCase, UpdateProductUseCase, DiscontinueProductUseCase {

    private final IdGenerator idGenerator;
    private final LoadProductPort loadProductPort;
    private final SaveProductPort saveProductPort;

    @Override
    public RegisterProductResult register(RegisterProductCommand command) {
        List<CreateProductImageCommand> imageCommands =
                command.images()
                       .stream()
                       .map(image -> new CreateProductImageCommand(image.imageUrl(), image.isThumbnail()))
                       .toList();

        Product product = Product.create(
                new CreateProductCommand(
                        command.name(),
                        command.description(),
                        command.price(),
                        command.stockQuantity(),
                        command.category(),
                        imageCommands
                ),
                new MemberId(command.sellerIdValue()),
                idGenerator
        );

        saveProductPort.save(product);

        return new RegisterProductResult(product.getId().value());
    }

    @Override
    public void addStock(AddStockCommand command) {
        Product product = loadProductPort.getById(new ProductId(command.productIdValue()));

        if (!product.isSeller(new MemberId(command.memberIdValue()))) {
            throw new NotProductSellerException();
        }

        product.addStock(command.quantity());
        saveProductPort.save(product);
    }

    @Override
    public void deductStock(DeductStockCommand command) {
        Product product = loadProductPort.getById(new ProductId(command.productIdValue()));

        if (!product.isSeller(new MemberId(command.memberIdValue()))) {
            throw new NotProductSellerException();
        }

        product.deductStock(command.quantity());
        saveProductPort.save(product);
    }

    @Override
    public void update(UpdateProductCommand command) {
        Product product = loadProductPort.getById(new ProductId(command.productIdValue()));

        if (!product.isSeller(new MemberId(command.memberIdValue()))) {
            throw new NotProductSellerException();
        }

        product.modify(new ModifyProductCommand(
                command.name(),
                command.description(),
                command.price(),
                command.category()
        ));

        saveProductPort.save(product);
    }

    @Override
    public void discontinue(DiscontinueProductCommand command) {
        Product product = loadProductPort.getById(new ProductId(command.productIdValue()));

        if (!product.isSeller(new MemberId(command.memberIdValue()))) {
            throw new NotProductSellerException();
        }

        product.discontinue();
        saveProductPort.save(product);
    }
}
