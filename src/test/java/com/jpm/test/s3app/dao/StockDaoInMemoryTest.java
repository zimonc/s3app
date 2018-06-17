package com.jpm.test.s3app.dao;

import com.jpm.test.s3app.model.Stock;
import com.jpm.test.s3app.model.StockCommon;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class StockDaoInMemoryTest
{
    @Test
    public void findBySymbol() throws Exception
    {
        Map<String, Stock> memoryMock = Mockito.mock(Map.class);
        StockDaoInMemory stockDaoInMemory = new StockDaoInMemory(memoryMock);
        String symbol = "symbol";
        Stock stockMock = Mockito.mock(Stock.class);
        Mockito.when(memoryMock.get(symbol)).thenReturn(stockMock);

        Stock actualStock = stockDaoInMemory.findBySymbol(symbol);

        Assert.assertEquals(stockMock, actualStock);
        Mockito.verify(memoryMock, Mockito.times(1)).get(symbol);
    }

    @Test
    public void findBySymbolEmpty() throws Exception
    {
        Map<String, Stock> memoryMock = Mockito.mock(Map.class);
        StockDaoInMemory stockDaoInMemory = new StockDaoInMemory(memoryMock);
        String symbol = "symbol";
        Mockito.when(memoryMock.get(symbol)).thenReturn(null);

        Stock actualStock = stockDaoInMemory.findBySymbol(symbol);

        Assert.assertEquals(null, actualStock);
        Mockito.verify(memoryMock, Mockito.times(1)).get(symbol);
    }

    @Test
    public void saveOrUpdate() throws Exception
    {
        Stock stock = new StockCommon("POP", 12, 100);
        Map<String, Stock> memoryMock = Mockito.mock(Map.class);
        StockDaoInMemory stockDaoInMemory = new StockDaoInMemory(memoryMock);

        stockDaoInMemory.saveOrUpdate(stock);

        Mockito.verify(memoryMock, Mockito.times(1)).put(stock.getSymbol(), stock);
    }
}