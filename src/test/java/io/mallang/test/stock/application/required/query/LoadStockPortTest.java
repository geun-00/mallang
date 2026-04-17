package io.mallang.test.stock.application.required.query;

import io.mallang.annotations.PortTest;
import io.mallang.product.domain.ProductId;
import io.mallang.stock.application.required.command.SaveStockPort;
import io.mallang.stock.application.required.query.LoadStockPort;
import io.mallang.stock.domain.Stock;
import io.mallang.stock.domain.exception.StockNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.mallang.assertions.StockAssertions.isSameAs;
import static io.mallang.fixtures.StockFixture.generateStock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@PortTest
@DisplayName("LoadStock Port")
class LoadStockPortTest {

    @Test
    void getByProductId로_Stock을_조회한다(
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadStockPort loadStockPort
    ) {
        // given
        Stock stock = generateStock(5);
        saveStockPort.save(stock);

        // when
        Stock loaded = loadStockPort.getByProductId(stock.getProductId());

        // then
        assertThat(loaded).satisfies(isSameAs(stock));
    }

    @Test
    void getByProductId는_존재하지_않는_상품_ID로_조회하면_StockNotFoundException이_발생한다(
            @Autowired LoadStockPort loadStockPort
    ) {
        // given
        ProductId unknownProductId = new ProductId("unknown-product-id");

        // when & then
        assertThatThrownBy(() -> loadStockPort.getByProductId(unknownProductId))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    void getAllByProductIds는_요청한_상품_ID들에_해당하는_Stock을_조회한다(
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadStockPort loadStockPort
    ) {
        // given
        Stock firstStock = generateStock(5);
        Stock secondStock = generateStock(10);
        saveStockPort.save(firstStock);
        saveStockPort.save(secondStock);

        List<ProductId> productIds = List.of(secondStock.getProductId(), firstStock.getProductId());

        // when
        List<Stock> loadedStocks = loadStockPort.getAllByProductIds(productIds);

        // then
        assertThat(loadedStocks)
                .extracting(
                        Stock::getProductId,
                        stock -> stock.getQuantity().value()
                )
                .containsExactlyInAnyOrder(
                        tuple(secondStock.getProductId(), secondStock.getQuantity().value()),
                        tuple(firstStock.getProductId(), firstStock.getQuantity().value())
                );
    }

    @Test
    void getAllByProductIds는_존재하지_않는_상품_ID가_포함되면_StockNotFoundException이_발생한다(
            @Autowired SaveStockPort saveStockPort,
            @Autowired LoadStockPort loadStockPort
    ) {
        // given
        Stock stock = generateStock(5);
        saveStockPort.save(stock);

        List<ProductId> productIds = List.of(stock.getProductId(), new ProductId("unknown-product-id"));

        // when & then
        assertThatThrownBy(() -> loadStockPort.getAllByProductIds(productIds)).isInstanceOf(StockNotFoundException.class);
    }
}
