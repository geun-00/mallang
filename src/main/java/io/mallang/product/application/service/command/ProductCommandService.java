package io.mallang.product.application.service.command;

import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.product.application.provided.command.RegisterProductUseCase;
import io.mallang.product.application.provided.command.model.RegisterProductCommand;
import io.mallang.product.application.provided.command.model.RegisterProductResult;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.domain.command.CreateProductCommand;
import io.mallang.product.domain.command.CreateProductImageCommand;
import io.mallang.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandService implements RegisterProductUseCase {

    private final IdGenerator idGenerator;
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
}
