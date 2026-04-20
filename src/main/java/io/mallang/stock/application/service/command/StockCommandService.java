package io.mallang.stock.application.service.command;

import io.mallang.member.domain.MemberId;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.application.provided.command.AddStockUseCase;
import io.mallang.stock.application.provided.command.DeductStockUseCase;
import io.mallang.stock.application.provided.command.model.AddStockCommand;
import io.mallang.stock.application.provided.command.model.DeductStockCommand;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.application.required.query.LoadStockForUpdatePort;
import io.mallang.stock.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockCommandService implements AddStockUseCase, DeductStockUseCase {

    private final SaveStockPort saveStockPort;
    private final LoadProductPort loadProductPort;
    private final LoadStockForUpdatePort loadStockForUpdatePort;

    @Override
    public void addStock(AddStockCommand command) {
        Product product = getProductForStockChange(command.productIdValue(), command.memberIdValue());

        Stock stock = loadStock(product);
        stock.add(command.quantity());
        saveStock(stock);
    }

    @Override
    public void deductStock(DeductStockCommand command) {
        Product product = getProductForStockChange(command.productIdValue(), command.memberIdValue());

        Stock stock = loadStock(product);
        stock.deduct(command.quantity());
        saveStock(stock);
    }

    private Product getProductForStockChange(String productIdValue, String memberIdValue) {
        Product product = loadProductPort.getById(new ProductId(productIdValue));

        product.validateSeller(new MemberId(memberIdValue));
        product.validateOrderable();

        return product;
    }

    private Stock loadStock(Product product) {
        return loadStockForUpdatePort.getByProductIdForUpdate(product.getId());
    }

    private void saveStock(Stock stock) {
        saveStockPort.save(stock);
    }
}
