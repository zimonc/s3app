package com.jpm.test.s3app.service;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.jpm.test.s3app.Constants;
import com.jpm.test.s3app.controller.TradeDto;
import com.jpm.test.s3app.dao.StockDao;
import com.jpm.test.s3app.dao.TradeDao;
import com.jpm.test.s3app.model.Stock;
import com.jpm.test.s3app.model.StockCommon;
import com.jpm.test.s3app.model.Trade;
import com.jpm.test.s3app.model.vo.TradeType;
import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import com.jpm.test.s3app.testutils.DomainAssertions;
import com.jpm.test.s3app.testutils.ThrowableCaptor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TradeServiceDefaultTest
{
    // CREATE TESTS

    @Test
    public void create() throws Exception
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeService = new TradeServiceDefault(tradeDaoMock, stockDaoMock);
        TradeDto tradeDto = new TradeDto("POP", TradeType.BUY, 1, 2.1);
        StockCommon stock = new StockCommon(tradeDto.getSymbol(), 8, 80);
        Mockito.when(stockDaoMock.findBySymbol(tradeDto.getSymbol())).thenReturn(stock);

        Trade trade = tradeService.create(tradeDto);

        Assert.assertNotNull(trade );
        Assert.assertTrue(trade.getStock().getClass().isAssignableFrom(StockCommon.class));
        DomainAssertions.assertEquals(stock, (StockCommon) trade.getStock());
        Assert.assertEquals(tradeDto.getPrice(), trade.getPrice(), 0);
        Assert.assertEquals(tradeDto.getType(), trade.getType());
        Assert.assertEquals(tradeDto.getQuantity(), trade.getQuantity(), 0);
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
    }

    @Test
    public void createWithInvalidSymbol()
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeService = new TradeServiceDefault(tradeDaoMock, stockDaoMock);
        TradeDto tradeDto = new TradeDto("POP", TradeType.BUY, 1, 2.1);
        Mockito.when(stockDaoMock.findBySymbol(tradeDto.getSymbol())).thenReturn(null);

        Throwable throwable = ThrowableCaptor.thrownBy(() -> tradeService.create(tradeDto));

        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(SymbolNotFoundException.class));
        Assert.assertEquals("Invalid symbol: POP", throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
    }

    private void createWithInvalidQuantity(int quantity)
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeService = new TradeServiceDefault(tradeDaoMock, stockDaoMock);
        TradeDto tradeDto = new TradeDto("POP", TradeType.BUY, quantity, 3.8);
        StockCommon stock = new StockCommon(tradeDto.getSymbol(), 8, 80);
        Mockito.when(stockDaoMock.findBySymbol(tradeDto.getSymbol())).thenReturn(stock);

        Throwable throwable = ThrowableCaptor.thrownBy(() -> tradeService.create(tradeDto));

        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(InvalidNumberException.class));
        Assert.assertEquals(String.format("Invalid quantity: %s", quantity), throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
    }

    private void createWithInvalidPrice(double price)
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeService = new TradeServiceDefault(tradeDaoMock, stockDaoMock);
        TradeDto tradeDto = new TradeDto("POP", TradeType.BUY, 18, price);
        StockCommon stock = new StockCommon(tradeDto.getSymbol(), 8, 80);
        Mockito.when(stockDaoMock.findBySymbol(tradeDto.getSymbol())).thenReturn(stock);

        Throwable throwable = ThrowableCaptor.thrownBy(() -> tradeService.create(tradeDto));

        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(InvalidNumberException.class));
        Assert.assertEquals(String.format("Invalid price: %s", price), throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
    }

    @Test
    public void createWithInvalidQuantity()
    {
        createWithInvalidQuantity(0);
        createWithInvalidQuantity(-2);
    }

    @Test
    public void createWithInvalidQPrice()
    {
        createWithInvalidPrice(0);
        createWithInvalidPrice(-2);
    }

    // CALCULATE VOLUME WEIGHTED STOCK PRICE TESTS

    @Test
    public void calculateVolumeWeightedStockPrice() throws SymbolNotFoundException
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeServiceMock = Mockito.spy(new TradeServiceDefault(tradeDaoMock, stockDaoMock));
        LocalDateTime timestamp = LocalDateTime.now();
        String symbol = "POP";
        Trade trade1 = new Trade(new Stock(symbol, 0, 0), null, 12, 72.0);
        Trade trade2 = new Trade(new Stock(symbol, 0, 0), null, 13, 73.0);
        Trade trade3 = new Trade(new Stock(symbol, 0, 0), null, 14, 74.0);
        Mockito.when(stockDaoMock.findBySymbol(symbol)).thenReturn(Mockito.mock(Stock.class));
        Predicate<Trade> predicate = Mockito.mock(Predicate.class);
        Mockito.when(tradeServiceMock.createVolumeWeightedStockPricePredicate(symbol)).thenReturn(predicate);
        Mockito.when(tradeDaoMock.findFromTimestampWithPredicate(timestamp, predicate))
            .thenReturn(Arrays.asList(trade1, trade2, trade3));

        BigDecimal vwsp = tradeServiceMock.calculateVolumeWeightedStockPrice("POP", timestamp);

        Assert.assertNotNull(vwsp);
        Assert.assertEquals(BigDecimal.valueOf(12 * 72.0 + 13 * 73.0 + 14 * 74.0).divide(BigDecimal.valueOf(12.0 + 13.0 + 14.0), Constants.GLOBAL_MATH_CONTEXT).doubleValue(), vwsp
            .doubleValue(), 0);
    }

    @Test
    public void calculateVolumeWeightedStockPriceWithInvalidSymbol()
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeServiceMock = Mockito.spy(new TradeServiceDefault(tradeDaoMock, stockDaoMock));
        LocalDateTime timestamp = LocalDateTime.now();
        String symbol = "POP";
        Mockito.when(stockDaoMock.findBySymbol(symbol)).thenReturn(null);

        Throwable throwable = ThrowableCaptor.thrownBy(() -> tradeServiceMock.calculateVolumeWeightedStockPrice(symbol, timestamp));

        Assert.assertNotNull(throwable);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(SymbolNotFoundException.class));
        Assert.assertEquals(String.format("Invalid symbol: %s", symbol), throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol(symbol);
    }

    @Test
    public void calculateVolumeWeightedStockPriceWithNoTrades() throws SymbolNotFoundException
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeServiceMock = Mockito.spy(new TradeServiceDefault(tradeDaoMock, stockDaoMock));
        LocalDateTime timestamp = LocalDateTime.now();
        String symbol = "POP";
        Mockito.when(stockDaoMock.findBySymbol(symbol)).thenReturn(Mockito.mock(Stock.class));
        Predicate<Trade> predicate = Mockito.mock(Predicate.class);
        Mockito.when(tradeServiceMock.createVolumeWeightedStockPricePredicate(symbol)).thenReturn(predicate);
        Mockito.when(tradeDaoMock.findFromTimestampWithPredicate(timestamp, predicate))
            .thenReturn(Arrays.asList());

        BigDecimal vwsp = tradeServiceMock.calculateVolumeWeightedStockPrice("POP", timestamp);

        Assert.assertNull(vwsp);
    }

    @Test
    public void createVolumeWeightedStockPricePredicate()
    {
        TradeService tradeService = new TradeServiceDefault(null, null);
        Trade trade1 = new Trade(new StockCommon("POP", 0, 0), TradeType.BUY, 0, 0);
        Trade trade2 = new Trade(new StockCommon("POP", 0, 0), TradeType.SELL, 0, 0);

        Assert.assertTrue(tradeService.createVolumeWeightedStockPricePredicate("POP").test(trade1));
        Assert.assertFalse(tradeService.createVolumeWeightedStockPricePredicate("TEA").test(trade1));
        Assert.assertFalse(tradeService.createVolumeWeightedStockPricePredicate("POP").test(trade2));
    }

    // CALCULATE ALL SHARE INDEX TESTS

    @Test
    public void calculateAllShareIndex()
    {
        TradeDao tradeDaoMock = Mockito.mock(TradeDao.class);
        TradeService tradeService = new TradeServiceDefault(tradeDaoMock, null);
        LocalDate localDate = LocalDateTime.now().toLocalDate();
        String symbol = "POP";
        List<Trade> tradeList = Arrays.asList(
            new Trade(null, null, 0, 2),
            new Trade(null, null, 0, 3),
            new Trade(null, null, 0, 4));
        Mockito.when(tradeDaoMock.findInDate(localDate)).thenReturn(tradeList);

        BigDecimal allShareIndex = tradeService.calculateAllShareIndex(localDate);

        Assert.assertNotNull(allShareIndex);
        Assert.assertEquals(
            BigDecimalMath.root(BigDecimal.valueOf(2 * 3 * 4), BigDecimal.valueOf(3), Constants.GLOBAL_MATH_CONTEXT),
            allShareIndex);

        Mockito.when(tradeDaoMock.findInDate(localDate)).thenReturn(tradeList);
    }
}