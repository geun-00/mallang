package io.mallang.cart.adapter.event;

import io.mallang.cart.application.required.command.SaveCartPort;
import io.mallang.cart.domain.Cart;
import io.mallang.member.application.event.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRegisteredEventHandler {

    private final SaveCartPort saveCartPort;

    @EventListener
    public void handle(MemberRegisteredEvent event) {
        saveCartPort.save(Cart.create(event.memberId()));
    }
}
