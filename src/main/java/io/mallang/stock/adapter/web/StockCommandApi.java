package io.mallang.stock.adapter.web;

import io.mallang.common.adapter.web.auth.CurrentMemberId;
import io.mallang.stock.adapter.web.model.AddStockRequest;
import io.mallang.stock.adapter.web.model.DeductStockRequest;
import io.mallang.stock.application.provided.command.AddStockUseCase;
import io.mallang.stock.application.provided.command.DeductStockUseCase;
import io.mallang.stock.application.provided.command.model.AddStockCommand;
import io.mallang.stock.application.provided.command.model.DeductStockCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
public class StockCommandApi {

    private final AddStockUseCase addStockUseCase;
    private final DeductStockUseCase deductStockUseCase;

    @PostMapping("/{productId}/add")
    public ResponseEntity<Void> addStock(
            @PathVariable String productId,
            @Valid @RequestBody AddStockRequest request,
            @CurrentMemberId String memberId
    ) {
        addStockUseCase.addStock(new AddStockCommand(
                memberId,
                productId,
                request.quantity()
        ));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/deduct")
    public ResponseEntity<Void> deductStock(
            @PathVariable String productId,
            @Valid @RequestBody DeductStockRequest request,
            @CurrentMemberId String memberId
    ) {
        deductStockUseCase.deductStock(new DeductStockCommand(
                memberId,
                productId,
                request.quantity()
        ));

        return ResponseEntity.noContent().build();
    }
}
