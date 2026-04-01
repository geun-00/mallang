package io.mallang.order.application.service.command;

import io.mallang.domain.common.ClockHolder;
import io.mallang.domain.common.IdGenerator;
import io.mallang.domain.common.vo.Address;
import io.mallang.domain.common.vo.Receiver;
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

import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderCommandService implements CreateOrderUseCase, CancelOrderUseCase {

    private final IdGenerator idGenerator;
    private final ClockHolder clockHolder;
    private final LoadMemberPort loadMemberPort;
    private final LoadOrderPort loadOrderPort;
    private final LoadProductPort loadProductPort;
    private final SaveProductPort saveProductPort;
    private final SaveOrderPort saveOrderPort;

    @Override
    public CreateOrderResult place(CreateOrderCommand command) {
        Member member = getOrderableMember(command.memberIdValue());
        // TODO : 일괄 로딩 및 일괄 저장 개선
        Map<String, Product> orderProducts = deductStocks(command.items());
        Order order = createOrder(command, member, orderProducts);

        orderProducts.values().forEach(saveProductPort::save);
        saveOrderPort.save(order);

        return new CreateOrderResult(order.getId().value());
    }

    private Member getOrderableMember(String memberIdValue) {
        Member member = loadMemberPort.getById(new MemberId(memberIdValue));

        if (!member.isOrderable()) {
            throw new NotOrderableMemberException(member.getId());
        }

        return member;
    }

    private Map<String, Product> deductStocks(List<CreateOrderItemCommand> items) {
        Map<String, Integer> quantitiesByProductId = items.stream()
                                                          .collect(toMap(
                                                                  CreateOrderItemCommand::productId,
                                                                  CreateOrderItemCommand::quantity,
                                                                  Integer::sum,
                                                                  LinkedHashMap::new
                                                          ));

        Map<String, Product> productsById = new LinkedHashMap<>();

        quantitiesByProductId.forEach((productId, quantity) -> {
            Product product = loadProductPort.getById(new ProductId(productId));
            product.deductStock(quantity);

            productsById.put(productId, product);
        });

        return productsById;
    }

    private Order createOrder(
            CreateOrderCommand command,
            Member member,
            Map<String, Product> orderProducts
    ) {
        List<PlaceOrderItemCommand> itemCommands = command.items().stream()
                                                          .map(item -> toDomainCommand(item, orderProducts))
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

    private PlaceOrderItemCommand toDomainCommand(CreateOrderItemCommand item, Map<String, Product> orderProducts) {
        Product product = orderProducts.get(item.productId());

        return new PlaceOrderItemCommand(
                product.getId(),
                item.quantity(),
                product.getPrice()
        );
    }


    @Override
    public void cancel(CancelOrderCommand command) {
        Order order = loadOrderPort.getById(new OrderId(command.orderIdValue()));

        if (!order.isOrderer(new MemberId(command.memberIdValue()))) {
            throw new NotOrdererException();
        }

        order.cancel();

        // TODO : 일괄 로딩 및 일괄 저장 개선
        Map<String, Product> productsById = addStocks(order.getItems());

        productsById.values().forEach(saveProductPort::save);
        saveOrderPort.save(order);
    }

    private Map<String, Product> addStocks(List<OrderItem> items) {
        Map<String, Integer> quantitiesByProductId = items.stream()
                                                          .collect(toMap(
                                                                  item -> item.getProductId().value(),
                                                                  OrderItem::getQuantity,
                                                                  Integer::sum,
                                                                  LinkedHashMap::new
                                                          ));
        Map<String, Product> productsById = new LinkedHashMap<>();

        quantitiesByProductId.forEach((productId, quantity) -> {
            Product product = loadProductPort.getById(new ProductId(productId));
            product.addStock(quantity);

            productsById.put(productId, product);
        });

        return productsById;
    }
}
