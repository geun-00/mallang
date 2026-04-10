package io.mallang.order.application.service.command;

import io.mallang.common.domain.port.ClockHolder;
import io.mallang.common.domain.port.IdGenerator;
import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Receiver;
import io.mallang.member.application.required.query.LoadMemberPort;
import io.mallang.member.domain.Member;
import io.mallang.member.domain.MemberId;
import io.mallang.member.domain.exception.NotOrderableMemberException;
import io.mallang.order.application.provided.command.CancelOrderUseCase;
import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.order.application.provided.command.model.CancelOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.application.provided.command.model.CreateOrderResult;
import io.mallang.order.application.required.command.SaveOrderPort;
import io.mallang.order.application.required.query.LoadOrderPort;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.OrderId;
import io.mallang.order.domain.OrderItem;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.order.domain.command.PlaceOrderItemCommand;
import io.mallang.order.domain.exception.NotOrdererException;
import io.mallang.product.application.required.command.SaveProductPort;
import io.mallang.product.application.required.query.LoadProductPort;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;
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
public class OrderCommandService implements CreateOrderUseCase, CancelOrderUseCase {

    private final IdGenerator idGenerator;
    private final ClockHolder clockHolder;
    private final LoadOrderPort loadOrderPort;
    private final LoadMemberPort loadMemberPort;
    private final LoadProductPort loadProductPort;
    private final SaveOrderPort saveOrderPort;
    private final SaveProductPort saveProductPort;

    @Override
    public CreateOrderResult place(CreateOrderCommand command) {
        Member member = getOrderableMember(command.memberIdValue());

        Map<String, Integer> quantitiesByProductId = aggregateQuantities(command.items());
        Map<String, Product> orderProducts = loadProductsById(quantitiesByProductId.keySet());

        deductStocks(orderProducts, quantitiesByProductId);

        Order order = createOrder(command, member, orderProducts);

        saveProducts(orderProducts.values());
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
            Map<String, Product> productsById,
            Map<String, Integer> quantitiesByProductId
    ) {
        quantitiesByProductId.forEach((productId, quantity) -> {
            Product product = productsById.get(productId);
            product.deductStock(quantity);
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

    @Override
    public void cancel(CancelOrderCommand command) {
        Order order = loadOrderPort.getById(new OrderId(command.orderIdValue()));

        if (!order.isOrderer(new MemberId(command.memberIdValue()))) {
            throw new NotOrdererException();
        }

        order.cancel();

        Map<String, Integer> quantitiesByProductId = aggregateOrderItemQuantities(order.getItems());
        Map<String, Product> productsById = loadProductsById(quantitiesByProductId.keySet());

        restoreStocks(productsById, quantitiesByProductId);

        saveProducts(productsById.values());
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
            Map<String, Product> productsById,
            Map<String, Integer> quantitiesByProductId
    ) {
        quantitiesByProductId.forEach((productId, quantity) -> {
            Product product = productsById.get(productId);
            product.addStock(quantity);
        });
    }

    private Map<String, Product> loadProductsById(Set<String> productIds) {
        List<ProductId> ids = productIds.stream()
                                        .map(ProductId::new)
                                        .toList();

        return loadProductPort.getAllByIds(ids)
                              .stream()
                              .collect(toMap(
                                      product -> product.getId().value(),
                                      product -> product,
                                      (left, right) -> left,
                                      LinkedHashMap::new
                              ));
    }

    private void saveProducts(Iterable<Product> products) {
        products.forEach(saveProductPort::save);
    }

    private void saveOrder(Order order) {
        saveOrderPort.save(order);
    }
}
