package io.mallang.test.stock.application.required.command;

import io.mallang.annotations.PortTest;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.application.required.query.LoadStockPort;
import io.mallang.stock.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.mallang.assertions.StockAssertions.isSameAs;
import static io.mallang.fixtures.StockFixture.generateStock;
import static org.assertj.core.api.Assertions.assertThat;

@PortTest
@DisplayName("SaveStock Port")
class SaveStockPortTest {

    @Test
    void 저장하면_조회된다(
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadStockPort loadStockPort
    ) {
        // given
        Stock stock = generateStock(5);

        // when
        saveStockPort.save(stock);

        // then
        Stock loaded = loadStockPort.getByProductId(stock.getProductId());
        assertThat(loaded).satisfies(isSameAs(stock));
    }

    @Test
    void 저장한_재고를_수정한_뒤_다시_저장하면_변경사항이_반영된다(
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadStockPort loadStockPort
    ) {
        // given
        Stock stock = generateStock(5);
        saveStockPort.save(stock);

        stock.add(3);

        // when
        saveStockPort.save(stock);

        // then
        Stock loaded = loadStockPort.getByProductId(stock.getProductId());
        assertThat(loaded).satisfies(isSameAs(stock));
    }
}
