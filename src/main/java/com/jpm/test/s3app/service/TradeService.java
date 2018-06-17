package com.jpm.test.s3app.service;

import com.jpm.test.s3app.controller.TradeDto;
import com.jpm.test.s3app.model.Trade;
import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public interface TradeService
{
    Trade create(TradeDto tradeDto) throws InvalidNumberException, SymbolNotFoundException;
    Predicate<Trade> createVolumeWeightedStockPricePredicate(String symbol);
    BigDecimal calculateVolumeWeightedStockPrice(String symbol, LocalDateTime timestamp) throws SymbolNotFoundException;
    BigDecimal calculateAllShareIndex(LocalDate localDate);
}
