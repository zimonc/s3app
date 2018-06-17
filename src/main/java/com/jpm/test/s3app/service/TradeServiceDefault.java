package com.jpm.test.s3app.service;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.jpm.test.s3app.Constants;
import com.jpm.test.s3app.controller.TradeDto;
import com.jpm.test.s3app.dao.StockDao;
import com.jpm.test.s3app.dao.TradeDao;
import com.jpm.test.s3app.model.Stock;
import com.jpm.test.s3app.model.Trade;
import com.jpm.test.s3app.model.vo.TradeType;
import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class TradeServiceDefault implements TradeService
{
    private final TradeDao tradeDao;
    private final StockDao stockDao;

    public TradeServiceDefault(final TradeDao tradeDao, final StockDao stockDao)
    {
        this.tradeDao = tradeDao;
        this.stockDao = stockDao;
    }

    @Override
    public Trade create(TradeDto tradeDto) throws InvalidNumberException, SymbolNotFoundException
    {
        Stock stock = findStockAndValidateInput(tradeDto.getSymbol(), tradeDto.getQuantity(), tradeDto.getPrice());
        Trade trade = new Trade(stock, tradeDto.getType(), tradeDto.getQuantity(), tradeDto.getPrice());
        tradeDao.create(trade);
        return trade;
    }

    /*
     * This method could be static method but I di it I did it an instance one to be
     * mock-able without going to `PowerMock` (which allows mocking static methods).
     */
    public Predicate<Trade> createVolumeWeightedStockPricePredicate(String symbol)
    {
        return (t) -> t.getStock().getSymbol().equals(symbol) && t.getType().equals(TradeType.BUY);
    }


    @Override
    public BigDecimal calculateVolumeWeightedStockPrice(String symbol, LocalDateTime timestamp) throws SymbolNotFoundException
    {
        findStock(symbol);
        List<Trade> tradeList = tradeDao.findFromTimestampWithPredicate(timestamp, createVolumeWeightedStockPricePredicate(symbol));
        if (tradeList.size() == 0)
        {
            return null;
        }
        BigDecimal numerator = BigDecimal.ZERO;
        BigDecimal denominator = BigDecimal.ZERO;
        for(Trade trade: tradeList)
        {
            numerator = numerator.add(BigDecimal.valueOf(trade.getPrice()).multiply(BigDecimal.valueOf(trade.getQuantity())));
            denominator = denominator.add(BigDecimal.valueOf(trade.getQuantity()));
        }
        return numerator.divide(denominator, Constants.GLOBAL_MATH_CONTEXT);
    }

    @Override
    public BigDecimal calculateAllShareIndex(LocalDate localDate)
    {
        List<Trade> tradeList = tradeDao.findInDate(localDate);
        if (tradeList.size() == 0)
        {
            return null;
        }
        BigDecimal allShareIndex = BigDecimal.ONE;
        for(Trade trade: tradeList)
        {
            allShareIndex = allShareIndex.multiply(new BigDecimal(trade.getPrice()));
        }
        return BigDecimalMath.root(allShareIndex, BigDecimal.valueOf(tradeList.size()), Constants.GLOBAL_MATH_CONTEXT);
    }


    private Stock findStockAndValidateInput(String symbol, int quantity, double price) throws SymbolNotFoundException, InvalidNumberException
    {
        Stock stock = findStock(symbol);
        if (quantity <= 0)
        {
            throw new InvalidNumberException(String.format("Invalid quantity: %s", quantity));
        }
        if (price <= 0)
        {
            throw new InvalidNumberException(String.format("Invalid price: %s", price));
        }
        return stock;
    }

    private Stock findStock(String symbol) throws SymbolNotFoundException
    {
        Stock stock = stockDao.findBySymbol(symbol);
        if (stock == null)
        {
            throw new SymbolNotFoundException(String.format("Invalid symbol: %s", symbol));
        }
        return stock;
    }
}
