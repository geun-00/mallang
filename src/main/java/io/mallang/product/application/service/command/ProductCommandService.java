package io.mallang.product.application.service.command;

import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.*;
import io.mallang.product.application.provided.command.model.*;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.*;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.CreateProductImageCommand;
import io.mallang.product.domain.command.ModifyProductCommand;
import io.mallang.product.domain.exception.NotProductSellerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService implements RegisterProductUseCase,
                                              AddStockUseCase,
                                              DeductStockUseCase,
                                              UpdateProductUseCase,
                                              DiscontinueProductUseCase {

    private final IdGenerator idGenerator;
    private final LoadProductPort loadProductPort;
    private final SaveProductPort saveProductPort;

    @Override
    public RegisterProductResult register(RegisterProductCommand command) {
        List<CreateProductImageCommand> imageCommands = command.images()
                                                               .stream()
                                                               .map(image -> new CreateProductImageCommand(
                                                                       new ImageUrl(image.imageUrl()),
                                                                       image.isThumbnail()
                                                               ))
                                                               .toList();

        Product product = Product.create(
                new CreateProductCommand(
                        new ProductName(command.name()),
                        new ProductDescription(command.description()),
                        new Money(command.price()),
                        new StockQuantity(command.stockQuantity()),
                        ProductCategory.from(command.category()),
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
        Product product = loadProductAndValidateSeller(command.productIdValue(), command.memberIdValue());

        product.addStock(command.quantity());
        saveProductPort.save(product);
    }

    @Override
    public void deductStock(DeductStockCommand command) {
        Product product = loadProductAndValidateSeller(command.productIdValue(), command.memberIdValue());

        product.deductStock(command.quantity());
        saveProductPort.save(product);
    }

    @Override
    public void update(UpdateProductCommand command) {
        Product product = loadProductAndValidateSeller(command.productIdValue(), command.memberIdValue());

        product.modify(new ModifyProductCommand(
                new ProductName(command.name()),
                new ProductDescription(command.description()),
                new Money(command.price()),
                ProductCategory.from(command.category())
        ));

        saveProductPort.save(product);
    }

    @Override
    public void discontinue(DiscontinueProductCommand command) {
        Product product = loadProductAndValidateSeller(command.productIdValue(), command.memberIdValue());

        product.discontinue();
        saveProductPort.save(product);
    }

    private Product loadProductAndValidateSeller(String productIdValue, String memberIdValue) {
        Product product = loadProductPort.getById(new ProductId(productIdValue));

        if (!product.isSeller(new MemberId(memberIdValue))) {
            throw new NotProductSellerException();
        }

        return product;
    }
}
