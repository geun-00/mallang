package io.mallang.order.adapter.persistence.mybatis.model;

public record SearchMyOrdersCondition(
        String memberId,
        String status,
        String lastOrderId,
        int limit
) {
}
