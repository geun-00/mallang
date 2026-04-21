package io.mallang.order.application.service.command;

import io.mallang.member.domain.MemberId;
import io.mallang.order.application.provided.command.CancelOrderUseCase;
import io.mallang.order.application.provided.command.model.CancelOrderCommand;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderForUpdatePort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.OrderItem;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.application.required.query.LoadStockForUpdatePort;
import io.mallang.stock.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelOrderCommandService implements CancelOrderUseCase {

    private final LoadOrderForUpdatePort loadOrderForUpdatePort;
    private final LoadStockForUpdatePort loadStockForUpdatePort;
    private final SaveOrderPort saveOrderPort;
    private final SaveStockPort saveStockPort;

    @Override
    public void cancel(CancelOrderCommand command) {
        Order order = loadOrderForUpdatePort.getByIdForUpdate(new OrderId(command.orderIdValue()));
        order.cancelBy(new MemberId(command.memberIdValue()));

        Map<String, Integer> quantitiesByProductId = aggregateOrderItemQuantities(order.getItems());
        Map<String, Stock> stocksByProductId = loadStocksByProductId(quantitiesByProductId.keySet());

        restoreStocks(stocksByProductId, quantitiesByProductId);

        saveStocks(stocksByProductId.values());
        saveOrder(order);
    }

    private Map<String, Integer> aggregateOrderItemQuantities(List<OrderItem> items) {
        return items.stream()
                    .collect(toMap(
                            item -> item.getProductId().value(),
                            OrderItem::getQuantity,
                            Integer::sum,
                            LinkedHashMap::new
                    ));
    }

    private void restoreStocks(
            Map<String, Stock> stocksByProductId,
            Map<String, Integer> quantitiesByProductId
    ) {
        quantitiesByProductId.forEach((productId, quantity) -> {
            Stock stock = stocksByProductId.get(productId);
            stock.add(quantity);
        });
    }

    private Map<String, Stock> loadStocksByProductId(Set<String> productIds) {
        List<ProductId> ids = productIds.stream()
                                        .map(ProductId::new)
                                        .toList();

        return loadStockForUpdatePort.getAllByProductIdsForUpdate(ids)
                                     .stream()
                                     .collect(toMap(
                                             stock -> stock.getProductId().value(),
                                             stock -> stock,
                                             (left, right) -> left,
                                             LinkedHashMap::new
                                     ));
    }

    private void saveStocks(Iterable<Stock> stocks) {
        stocks.forEach(saveStockPort::save);
    }

    private void saveOrder(Order order) {
        saveOrderPort.save(order);
    }
}
