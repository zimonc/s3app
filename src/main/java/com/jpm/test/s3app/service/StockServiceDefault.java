package com.jpm.test.s3app.service;

import com.jpm.test.s3app.Constants;
import com.jpm.test.s3app.dao.StockDao;
import com.jpm.test.s3app.model.Stock;
import com.jpm.test.s3app.model.StockCommon;
import com.jpm.test.s3app.model.StockPreferred;
import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import java.awt.Container;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockServiceDefault implements StockService
{
    private static final Logger logger = LoggerFactory.getLogger(StockServiceDefault.class);

    private final StockDao stockDao;

    public StockServiceDefault(final StockDao stockDao)
    {
        this.stockDao = stockDao;
    }

    private BigDecimal calculateDividendYield(StockCommon stock, double marketPrice)
    {
        return new BigDecimal(stock.getLastDividend())
            .divide(new BigDecimal(marketPrice), Constants.GLOBAL_MATH_CONTEXT);
    }

    private BigDecimal calculateDividendYield(StockPreferred stock, double marketPrice)
    {
        return new BigDecimal(stock.getFixedDividend())
            .multiply(new BigDecimal(stock.getParValue(), Constants.GLOBAL_MATH_CONTEXT)
            .divide(new BigDecimal(100.0)), Constants.GLOBAL_MATH_CONTEXT)
            .divide(new BigDecimal(marketPrice), Constants.GLOBAL_MATH_CONTEXT);
    }

    @Override
    public BigDecimal calculateDividendYield(String symbol, double marketPrice) throws SymbolNotFoundException, InvalidNumberException
    {
        Stock stock = findStockAndValidateInput(symbol, marketPrice);
        if (stock.getClass().isAssignableFrom(StockCommon.class))
        {
            return calculateDividendYield((StockCommon) stock, marketPrice);
        }
        else
        {
            return calculateDividendYield((StockPreferred) stock, marketPrice);
        }
    }

    private BigDecimal calculatePriceEarningRatio(StockCommon stock, double marketPrice)
    {
        return new BigDecimal(marketPrice).divide(new BigDecimal(stock.getLastDividend()), Constants.GLOBAL_MATH_CONTEXT);
    }

    private BigDecimal calculatePriceEarningRatio(StockPreferred stock, double marketPrice)
    {
        // CONTOLLA NEL CASO DEI PREFERRED!!!!
        logger.debug(String.format("last_dividend=%s fixed_dividend=%s, par_value=%", stock.getLastDividend(), stock.getFixedDividend(), stock.getParValue()));
        BigDecimal dividend = new BigDecimal(stock.getLastDividend())
            .subtract(new BigDecimal(stock.getFixedDividend())
            .divide(new BigDecimal(100.0),  Constants.GLOBAL_MATH_CONTEXT));
        logger.debug(String.format("market_price=%s dividend=%s", marketPrice, dividend));
        return new BigDecimal(marketPrice).divide(dividend, Constants.GLOBAL_MATH_CONTEXT);
    }

    @Override
    public BigDecimal calculatePriceEarningRatio(String symbol, double marketPrice) throws SymbolNotFoundException, InvalidNumberException
    {
        Stock stock = findStockAndValidateInput(symbol, marketPrice);
        if (stock.getClass().isAssignableFrom(StockCommon.class))
        {
            return calculatePriceEarningRatio((StockCommon) stock, marketPrice);
        }
        else
        {
            return calculatePriceEarningRatio((StockPreferred) stock, marketPrice);
        }
    }

    private Stock findStockAndValidateInput(String symbol, double marketPrice) throws SymbolNotFoundException, InvalidNumberException
    {
        Stock stock = stockDao.findBySymbol(symbol);
        if (stock == null)
        {
            throw new SymbolNotFoundException(String.format("Invalid symbol: %s", symbol));
        }
        if (marketPrice <= 0) {
            throw new InvalidNumberException(String.format("Invalid market price: %s", marketPrice));
        }
        return stock;
    }
}
