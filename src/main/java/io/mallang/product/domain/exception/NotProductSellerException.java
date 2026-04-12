package io.mallang.product.domain.exception;

import io.mallang.common.domain.exception.ForbiddenException;
import io.mallang.member.domain.MemberId;
import io.mallang.product.domain.ProductId;

public class NotProductSellerException extends ForbiddenException {

    public NotProductSellerException(ProductId productId, MemberId requesterId, MemberId sellerId) {
        super(
                "상품의 판매자가 아닙니다.",
                "상품의 판매자가 아닙니다 => productId: %s, requesterId: %s, sellerId: %s".formatted(
                        productId.value(),
                        requesterId.value(),
                        sellerId.value()
                )
        );
    }
}
