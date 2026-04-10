package io.mallang.order.adapter.web;

import io.mallang.common.adapter.web.auth.CurrentMemberId;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.provided.command.CancelOrderUseCase;
import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.order.application.provided.command.model.CancelOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.application.provided.command.model.CreateOrderResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my/orders")
public class OrderCommandApi {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody CreateOrderRequest request,
            @CurrentMemberId String memberId
    ) {
        List<CreateOrderItemCommand> items = request.items()
                                                    .stream()
                                                    .map(item -> new CreateOrderItemCommand(
                                                            item.productId(),
                                                            item.quantity()
                                                    ))
                                                    .toList();

        CreateOrderResult result = createOrderUseCase.place(
                new CreateOrderCommand(
                        memberId,
                        items,
                        request.receiverName(),
                        request.receiverPhoneNumber(),
                        request.zipCode(),
                        request.mainAddress(),
                        request.detailAddress()
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                  .path("/{id}")
                                                  .buildAndExpand(result.orderId())
                                                  .toUri();

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable String orderId,
            @CurrentMemberId String memberId
    ) {
        cancelOrderUseCase.cancel(new CancelOrderCommand(
                orderId,
                memberId
        ));

        return ResponseEntity.noContent().build();
    }
}
