package io.mallang.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.mallang.init.ProductSeeder.SellerProducts;

@Component
@Profile("local")
@RequiredArgsConstructor
public class TestDataInitializer {

    private final MemberSeeder memberSeeder;
    private final ProductSeeder productSeeder;
    private final OrderSeeder orderSeeder;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (memberSeeder.isAlreadySeeded()) {
            return;
        }

        List<String> memberIds = memberSeeder.seed();
        List<SellerProducts> productIdsByMemberIndex = productSeeder.seed(memberIds);
        orderSeeder.seed(memberIds, productIdsByMemberIndex);
    }
}
