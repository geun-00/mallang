package io.mallang.order.domain.exception;

import io.mallang.common.domain.exception.ForbiddenException;

public class NotOrdererException extends ForbiddenException {

    public NotOrdererException() {
        super("주문자가 아닙니다.");
    }
}
