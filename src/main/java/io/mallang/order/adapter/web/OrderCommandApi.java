package io.mallang.order.adapter.web;

import io.mallang.member.adapter.security.CustomUserDetails;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.application.provided.command.model.CreateOrderResult;
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
public class OrderCommandApi {

    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping("/my/orders")
    public ResponseEntity<Void> create(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CreateOrderItemCommand> items = request.items().stream()
                                                    .map(item -> new CreateOrderItemCommand(
                                                            item.productId(),
                                                            item.quantity()
                                                    ))
                                                    .toList();

        CreateOrderResult result = createOrderUseCase.place(
                new CreateOrderCommand(
                        userDetails.getMemberIdValue(),
                        items,
                        request.receiverName(),
                        request.receiverPhoneNumber(),
                        request.zipCode(),
                        request.mainAddress(),
                        request.detailAddress()
                )
        );

        return ResponseEntity.created(URI.create("/my/orders/" + result.orderId())).build();
    }
}
