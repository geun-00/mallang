package io.mallang.init;

import io.mallang.order.application.provided.command.CreateOrderUseCase;
import io.mallang.order.application.provided.command.model.CreateOrderCommand;
import io.mallang.order.application.provided.command.model.CreateOrderItemCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static io.mallang.init.ProductSeeder.SellerProducts;

@Component
@Profile("local")
@RequiredArgsConstructor
class OrderSeeder {

    private static final int ORDER_COUNT_PER_MEMBER = 10;

    private final CreateOrderUseCase createOrderUseCase;

    void seed(List<String> memberIds, List<SellerProducts> sellerProducts) {
        IntStream.range(0, memberIds.size())
                 .forEach(memberIndex -> placeOrders(memberIds, sellerProducts, memberIndex));
    }

    private void placeOrders(List<String> memberIds, List<SellerProducts> sellerProducts, int memberIndex) {
        String memberId = memberIds.get(memberIndex);

        List<String> orderableProductIds = sellerProducts.get(nextSellerIndex(memberIndex, memberIds.size()))
                                                         .productIds();

        IntStream.range(0, ORDER_COUNT_PER_MEMBER)
                 .forEach(orderIndex -> placeOrder(memberId, orderableProductIds, orderIndex));
    }

    private int nextSellerIndex(int memberIndex, int memberCount) {
        return (memberIndex + 1) % memberCount;
    }

    private void placeOrder(String memberId, List<String> orderableProductIds, int orderIndex) {
        createOrderUseCase.place(new CreateOrderCommand(
                memberId,
                createOrderItems(orderableProductIds, orderIndex),
                "홍길동",
                "01012345678",
                "12345",
                "서울시 강남구 테헤란로 1",
                "101호"
        ));
    }

    private List<CreateOrderItemCommand> createOrderItems(List<String> orderableProductIds, int orderIndex) {
        List<String> productIds = pickProductIds(orderableProductIds, orderIndex);

        return productIds.stream()
                         .map(productId -> new CreateOrderItemCommand(productId, randomOrderItemQuantity()))
                         .toList();
    }

    private List<String> pickProductIds(List<String> orderableProductIds, int orderIndex) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        final int maxOrderItemCount = 10;

        int maxItemCount = Math.min(maxOrderItemCount, orderableProductIds.size());
        int itemCount = random.nextInt(1, maxItemCount + 1);
        int startProductIndex = (orderIndex * maxOrderItemCount) % orderableProductIds.size();

        return IntStream.range(0, itemCount)
                        .mapToObj(itemIndex -> orderableProductIds.get((startProductIndex + itemIndex) % orderableProductIds.size()))
                        .toList();
    }

    private int randomOrderItemQuantity() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        return random.nextInt(1, 10 + 1);
    }
}
