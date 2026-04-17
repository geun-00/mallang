package io.mallang.stock.application.required.command;

import io.mallang.stock.domain.Stock;

public interface SaveStockPort {

    void save(Stock stock);
}
