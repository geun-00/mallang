package io.mallang.order.domain.exception;

import io.mallang.common.domain.exception.ForbiddenException;
import io.mallang.member.domain.MemberId;
import io.mallang.order.domain.OrderId;

public class NotOrdererException extends ForbiddenException {

    public NotOrdererException(OrderId orderId, MemberId requesterId, MemberId ordererId) {
        super(
                "주문자가 아닙니다.",
                "주문자가 아닙니다 => orderId: %s, requesterId: %s, ordererId: %s".formatted(
                        orderId.value(),
                        requesterId.value(),
                        ordererId.value()
                )
        );
    }
}
