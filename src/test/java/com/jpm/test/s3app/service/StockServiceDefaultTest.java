package com.jpm.test.s3app.service;

import com.jpm.test.s3app.Constants;
import com.jpm.test.s3app.dao.StockDao;
import com.jpm.test.s3app.model.Stock;
import com.jpm.test.s3app.model.StockCommon;
import com.jpm.test.s3app.model.StockPreferred;
import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import com.jpm.test.s3app.testutils.ThrowableCaptor;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class StockServiceDefaultTest
{
    // DIVIDEND YIELD TESTS

    @Test
    public void calculateDividendYieldWithCommonStock() throws Exception
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Stock stock = new StockCommon("POP", 8, 100);
        Mockito.when(stockDaoMock.findBySymbol(stock.getSymbol())).thenReturn(stock);

        BigDecimal dividendYield = stockServiceDefault.calculateDividendYield(stock.getSymbol(), 2.0);

        Assert.assertEquals(dividendYield, BigDecimal.valueOf(4));
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol(stock.getSymbol());
    }

    @Test
    public void calculateDividendYieldWithPreferredStock() throws Exception
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Stock stock = new StockPreferred("POP", 8, 2, 100);
        Mockito.when(stockDaoMock.findBySymbol(stock.getSymbol())).thenReturn(stock);

        BigDecimal dividendYield = stockServiceDefault.calculateDividendYield(stock.getSymbol(), 2.0);

        Assert.assertEquals(new BigDecimal(1.0, Constants.GLOBAL_MATH_CONTEXT), dividendYield);
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol(stock.getSymbol());
    }

    @Test
    public void calculateDividendYieldWithInvalidSymbol() throws Exception
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Mockito.when(stockDaoMock.findBySymbol("POP")).thenReturn(null);

        Throwable throwable = ThrowableCaptor.thrownBy(() -> stockServiceDefault.calculateDividendYield("POP", 2.0));

        Assert.assertTrue(throwable != null);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(SymbolNotFoundException.class));
        Assert.assertEquals("Invalid symbol: POP", throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
    }

    private void calculateDividendYieldWithInvalidNumber(double number)
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Mockito.when(stockDaoMock.findBySymbol("POP")).thenReturn(Mockito.mock(Stock.class));

        Throwable throwable = ThrowableCaptor.thrownBy(() -> stockServiceDefault.calculateDividendYield("POP", number));

        Assert.assertTrue(throwable != null);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(InvalidNumberException.class));
        Assert.assertEquals(String.format("Invalid market price: %s", number), throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
        Mockito.reset(stockDaoMock);
    }

    @Test
    public void calculateDividendYieldWithInvalidNumber() throws Exception
    {
        calculateDividendYieldWithInvalidNumber(-2.0);
        calculateDividendYieldWithInvalidNumber(0.0);
    }

    // PRICE EARNING RATIO TESTS

    @Test
    public void calculatePriceEarningRatioWithCommonStock() throws Exception
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Stock stock = new StockCommon("POP", 8, 100);
        Mockito.when(stockDaoMock.findBySymbol(stock.getSymbol())).thenReturn(stock);

        BigDecimal dividendYield = stockServiceDefault.calculatePriceEarningRatio(stock.getSymbol(), 2.0);

        Assert.assertEquals(new BigDecimal(0.25, Constants.GLOBAL_MATH_CONTEXT), dividendYield);
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol(stock.getSymbol());
    }

    @Test
    public void calculatePriceEarningRatioWithPreferredStock() throws Exception
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Stock stock = new StockPreferred("POP", 8, 2, 100);
        Mockito.when(stockDaoMock.findBySymbol(stock.getSymbol())).thenReturn(stock);

        BigDecimal dividendYield = stockServiceDefault.calculateDividendYield(stock.getSymbol(), 2.0);

        Assert.assertEquals(new BigDecimal(2.0).divide(new BigDecimal(8 - 6), Constants.GLOBAL_MATH_CONTEXT), dividendYield);
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol(stock.getSymbol());
    }

    @Test
    public void calculatePriceEarningRatioWithInvalidSymbol() throws Exception
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Mockito.when(stockDaoMock.findBySymbol("POP")).thenReturn(null);

        Throwable throwable = ThrowableCaptor.thrownBy(() -> stockServiceDefault.calculatePriceEarningRatio("POP", 2.0));

        Assert.assertTrue(throwable != null);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(SymbolNotFoundException.class));
        Assert.assertEquals("Invalid symbol: POP", throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
    }

    private void calculatePriceEarningRatioWithInvalidNumber(double number)
    {
        StockDao stockDaoMock = Mockito.mock(StockDao.class);
        StockServiceDefault stockServiceDefault = new StockServiceDefault(stockDaoMock);
        Mockito.when(stockDaoMock.findBySymbol("POP")).thenReturn(Mockito.mock(Stock.class));

        Throwable throwable = ThrowableCaptor.thrownBy(() -> stockServiceDefault.calculatePriceEarningRatio("POP", number));

        Assert.assertTrue(throwable != null);
        Assert.assertTrue(throwable.getClass().isAssignableFrom(InvalidNumberException.class));
        Assert.assertEquals(String.format("Invalid market price: %s", number), throwable.getMessage());
        Mockito.verify(stockDaoMock, Mockito.times(1)).findBySymbol("POP");
        Mockito.reset(stockDaoMock);
    }

    @Test
    public void calculatePriceEarningRatioWithInvalidNumber() throws Exception
    {
        calculatePriceEarningRatioWithInvalidNumber(-2.0);
        calculatePriceEarningRatioWithInvalidNumber(0.0);
    }
}