package io.mallang.fixtures;

import io.mallang.domain.common.ClockHolder;
import io.mallang.domain.common.IdGenerator;
import io.mallang.member.domain.MemberId;
import io.mallang.order.adapter.web.model.CreateOrderRequest;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import io.mallang.order.domain.Order;
import io.mallang.order.domain.command.PlaceOrderCommand;
import io.mallang.order.domain.command.PlaceOrderItemCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.mallang.order.adapter.web.model.CreateOrderRequest.CreateOrderItemRequest;

public class OrderFixture {

    public static IdGenerator generateIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    public static ClockHolder generateClockHolder() {
        return () -> LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    }

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

    public static PlaceOrderCommand generatePlaceOrderCommand() {
        return generatePlaceOrderCommand(generateOrderItemCommands(1));
    }

    public static PlaceOrderCommand generatePlaceOrderCommand(List<PlaceOrderItemCommand> items) {
        return new PlaceOrderCommand(
                UUID.randomUUID().toString(),
                items,
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        );
    }

    public static List<PlaceOrderItemCommand> generateOrderItemCommands(int count) {
        List<PlaceOrderItemCommand> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(generateOrderItemCommand());
        }
        return items;
    }

    public static List<CreateOrderItemCommand> generateCreateOrderItemCommands(int count) {
        List<CreateOrderItemCommand> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(generateOrderPlacementItemCommand());
        }
        return items;
    }

    public static PlaceOrderItemCommand generateOrderItemCommand() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new PlaceOrderItemCommand(
                UUID.randomUUID().toString(),
                random.nextInt(1, 10),
                BigDecimal.valueOf(random.nextInt(1000, 100000))
        );
    }

    public static CreateOrderItemCommand generateOrderPlacementItemCommand() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new CreateOrderItemCommand(
                UUID.randomUUID().toString(),
                random.nextInt(1, 10)
        );
    }

    public static Order generateOrder() {
        return Order.place(generatePlaceOrderCommand(), generateIdGenerator(), generateClockHolder());
    }

    public static Order generateCanceledOrder() {
        Order order = generateOrder();
        order.cancel();
        return order;
    }

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
}
