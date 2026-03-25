package io.mallang.product.domain.exception;

import io.mallang.domain.common.exception.ForbiddenException;

public class NotProductSellerException extends ForbiddenException {

    public NotProductSellerException() {
        super("상품의 판매자가 아닙니다.");
    }
}
