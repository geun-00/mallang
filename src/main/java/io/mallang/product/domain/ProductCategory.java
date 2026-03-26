package io.mallang.product.domain;

import io.mallang.domain.common.exception.InvalidValueException;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public enum ProductCategory {
    FOOD,
    ELECTRONICS,
    CLOTHING,
    BOOKS,
    ETC;

    public static ProductCategory from(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidValueException("카테고리는 필수입니다.");
        }

        return Arrays.stream(values())
                     .filter(c -> c.name().equalsIgnoreCase(value))
                     .findFirst()
                     .orElseThrow(() -> new InvalidValueException(
                             "유효하지 않은 카테고리입니다: " + value + ". 허용값: " + Arrays.stream(values()).map(ProductCategory::name).collect(joining(", "))
                     ));
    }
}
