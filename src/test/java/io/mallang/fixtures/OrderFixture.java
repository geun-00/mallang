package io.mallang.fixtures;

import io.mallang.common.domain.vo.Address;
import io.mallang.common.domain.vo.Money;
import io.mallang.common.domain.vo.Receiver;
import io.mallang.member.domain.MemberId;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.order.domain.command.PlaceOrderItemCommand;
import io.mallang.product.domain.Product;
import io.mallang.product.domain.ProductId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.mallang.fixtures.CommonFixture.generateClockHolder;
import static io.mallang.fixtures.CommonFixture.generateIdGenerator;
import static io.mallang.order.adapter.web.model.CreateOrderRequest.CreateOrderItemRequest;

public class OrderFixture {

    // =====================================================================
    // 웹 요청 모델 (Web Request Models)
    // =====================================================================

    public static CreateOrderRequest generateCreateOrderRequest(String productId, Integer quantity) {
        return new CreateOrderRequest(
                List.of(new CreateOrderItemRequest(productId, quantity)),
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );
    }

    // =====================================================================
    // 애플리케이션 커맨드 (Application Commands)
    // =====================================================================

    public static CreateOrderCommand generateCreateOrderCommand(MemberId memberId) {
        return generateCreateOrderCommand(memberId, generateCreateOrderItemCommands(1));
    }

    public static CreateOrderCommand generateCreateOrderCommand(MemberId memberId, List<CreateOrderItemCommand> items) {
        return new CreateOrderCommand(
                memberId.value(),
                items,
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );
    }

    public static List<CreateOrderItemCommand> generateCreateOrderItemCommands(int count) {
        List<CreateOrderItemCommand> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(generateOrderPlacementItemCommand());
        }
        return items;
    }

    public static CreateOrderItemCommand generateOrderPlacementItemCommand() {
        return new CreateOrderItemCommand(
                UUID.randomUUID().toString(),
                generateOrderQuantity()
        );
    }

    // =====================================================================
    // 도메인 커맨드 (Domain Commands)
    // =====================================================================

    public static PlaceOrderCommand generatePlaceOrderCommand() {
        return generatePlaceOrderCommand(generateOrderItemCommands(1));
    }

    public static PlaceOrderCommand generatePlaceOrderCommand(MemberId memberId, List<PlaceOrderItemCommand> items) {
        return new PlaceOrderCommand(
                memberId,
                items,
                generateReceiver(),
                generateAddress()
        );
    }

    public static PlaceOrderCommand generatePlaceOrderCommand(List<PlaceOrderItemCommand> items) {
        return new PlaceOrderCommand(
                new MemberId(UUID.randomUUID().toString()),
                items, generateReceiver(), generateAddress()
        );
    }

    public static List<PlaceOrderItemCommand> generateOrderItemCommands(int count) {
        List<PlaceOrderItemCommand> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(generateOrderItemCommand());
        }
        return items;
    }

    public static PlaceOrderItemCommand generateOrderItemCommand() {
        return new PlaceOrderItemCommand(
                new ProductId(UUID.randomUUID().toString()),
                generateOrderQuantity(),
                new Money(generatePriceAmount())
        );
    }

    // =====================================================================
    // 도메인 객체 (Domain Objects)
    // =====================================================================

    public static Order generateOrder() {
        return Order.place(generatePlaceOrderCommand(), generateIdGenerator(), generateClockHolder());
    }

    public static Order generateOrder(MemberId memberId, Product product, int quantity) {
        List<PlaceOrderItemCommand> items = List.of(new PlaceOrderItemCommand(product.getId(), quantity, product.getPrice()));
        return generateOrderWithItems(memberId, items);
    }

    public static Order generateOrderWithItems(MemberId memberId, List<PlaceOrderItemCommand> items) {
        return Order.place(generatePlaceOrderCommand(memberId, items), generateIdGenerator(), generateClockHolder());
    }

    public static Order generateCanceledOrder() {
        Order order = generateOrder();
        order.cancelBy(order.getMemberId());
        return order;
    }
    public static Order generateCanceledOrder(MemberId memberId, Product product, int quantity) {
        Order order = generateOrder(memberId, product, quantity);
        order.cancelBy(memberId);
        return order;
    }

    public static Receiver generateReceiver() {
        return new Receiver("홍길동", "01012345678");
    }

    public static Address generateAddress() {
        return new Address("12345", "서울시 강남구 테헤란로 1", "101호");
    }

    // =====================================================================
    // 랜덤 원시값 (Random Primitives)
    // =====================================================================
    public static int generateOrderQuantity() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(1, 10);
    }

    public static BigDecimal generatePriceAmount() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return BigDecimal.valueOf(random.nextInt(1000, 100000));
    }
}
