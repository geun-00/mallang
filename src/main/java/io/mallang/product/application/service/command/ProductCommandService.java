package io.mallang.product.application.service.command;

import io.mallang.common.application.event.EventPublisher;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.vo.Money;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.DiscontinueProductUseCase;
import io.mallang.product.application.provided.command.RegisterProductUseCase;
import io.mallang.product.application.provided.command.UpdateProductUseCase;
import io.mallang.product.application.provided.command.model.DiscontinueProductCommand;
import io.mallang.product.application.provided.command.model.ProductRegisteredEvent;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductResult;
import io.mallang.product.application.provided.command.model.UpdateProductCommand;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.ImageUrl;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductCategory;
import io.mallang.product.domain.ProductDescription;
import io.mallang.product.domain.ProductId;
import io.mallang.product.domain.ProductName;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.CreateProductImageCommand;
import io.mallang.product.domain.command.ModifyProductCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService implements RegisterProductUseCase,
                                              UpdateProductUseCase,
                                              DiscontinueProductUseCase {

    private final IdGenerator idGenerator;
    private final EventPublisher eventPublisher;
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
                        ProductCategory.from(command.category()),
                        imageCommands
                ),
                new MemberId(command.sellerIdValue()),
                idGenerator
        );

        saveProductPort.save(product);
        eventPublisher.publish(new ProductRegisteredEvent(product.getId().value(), command.stockQuantity()));

        return new RegisterProductResult(product.getId().value());
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
        product.validateSeller(new MemberId(memberIdValue));

        return product;
    }
}
