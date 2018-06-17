package com.jpm.test.s3app.service;

import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import java.math.BigDecimal;
public interface StockService
{
    BigDecimal calculateDividendYield(String symbol, double marketPrice) throws SymbolNotFoundException, InvalidNumberException;

    BigDecimal calculatePriceEarningRatio(String symbol, double marketPrice)  throws SymbolNotFoundException, InvalidNumberException;
}
