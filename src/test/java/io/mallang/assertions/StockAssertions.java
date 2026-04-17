package io.mallang.assertions;

import io.mallang.stock.domain.Stock;
import org.assertj.core.api.ThrowingConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class StockAssertions {

    public static ThrowingConsumer<Stock> isSameAs(Stock expected) {
        return actual -> {
            assertThat(actual.getProductId()).isEqualTo(expected.getProductId());
            assertThat(actual.getQuantity().value()).isEqualTo(expected.getQuantity().value());
        };
    }
}
