package io.mallang.order.application.service.command;

import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.exception.NotOrderableMemberException;
import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.application.provided.command.model.CreateOrderResult;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.order.domain.command.PlaceOrderItemCommand;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
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
public class CreateOrderCommandService implements CreateOrderUseCase {

    private final IdGenerator idGenerator;
    private final ClockHolder clockHolder;
    private final LoadMemberPort loadMemberPort;
    private final LoadProductPort loadProductPort;
    private final LoadStockForUpdatePort loadStockForUpdatePort;
    private final SaveOrderPort saveOrderPort;
    private final SaveStockPort saveStockPort;

    @Override
    public CreateOrderResult place(CreateOrderCommand command) {
        Member member = getOrderableMember(command.memberIdValue());

        Map<String, Integer> quantitiesByProductId = aggregateQuantities(command.items());
        Map<String, Product> orderProducts = loadOrderableProductsById(quantitiesByProductId.keySet());
        Map<String, Stock> orderStocks = loadStocksByProductId(quantitiesByProductId.keySet());

        deductStocks(orderStocks, quantitiesByProductId);

        Order order = createOrder(command, member, orderProducts);

        saveStocks(orderStocks.values());
        saveOrder(order);

        return new CreateOrderResult(order.getId().value());
    }

    private Member getOrderableMember(String memberIdValue) {
        Member member = loadMemberPort.getById(new MemberId(memberIdValue));

        if (!member.isOrderable()) {
            throw new NotOrderableMemberException(member.getId());
        }

        return member;
    }

    private Map<String, Integer> aggregateQuantities(List<CreateOrderItemCommand> items) {
        return items.stream()
                    .collect(toMap(
                            CreateOrderItemCommand::productId,
                            CreateOrderItemCommand::quantity,
                            Integer::sum,
                            LinkedHashMap::new
                    ));
    }

    private void deductStocks(
            Map<String, Stock> stocksByProductId,
            Map<String, Integer> quantitiesByProductId
    ) {
        quantitiesByProductId.forEach((productId, quantity) -> {
            Stock stock = stocksByProductId.get(productId);
            stock.deduct(quantity);
        });
    }

    private Order createOrder(
            CreateOrderCommand command,
            Member member,
            Map<String, Product> orderProducts
    ) {
        List<PlaceOrderItemCommand> itemCommands = command.items()
                                                          .stream()
                                                          .map(item -> {
                                                              Product product = orderProducts.get(item.productId());

                                                              return new PlaceOrderItemCommand(
                                                                      product.getId(),
                                                                      item.quantity(),
                                                                      product.getPrice()
                                                              );
                                                          })
                                                          .toList();
        return Order.place(
                new PlaceOrderCommand(
                        member.getId(),
                        itemCommands,
                        new Receiver(command.receiverName(), command.receiverPhoneNumber()),
                        new Address(command.zipCode(), command.mainAddress(), command.detailAddress())
                ),
                idGenerator,
                clockHolder
        );
    }

    private Map<String, Product> loadOrderableProductsById(Set<String> productIds) {
        List<ProductId> ids = productIds.stream()
                                        .map(ProductId::new)
                                        .toList();

        LinkedHashMap<String, Product> productsById = loadProductPort.getAllByIds(ids)
                                                                     .stream()
                                                                     .collect(toMap(
                                                                             product -> product.getId().value(),
                                                                             product -> product,
                                                                             (left, right) -> left,
                                                                             LinkedHashMap::new
                                                                     ));
        productsById.values().forEach(Product::validateOrderable);

        return productsById;
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
