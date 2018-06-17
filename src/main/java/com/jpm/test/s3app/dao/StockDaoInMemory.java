package com.jpm.test.s3app.dao;

import com.jpm.test.s3app.model.Stock;
import java.util.Map;

public class StockDaoInMemory implements StockDao
{
    private Map<String, Stock> memory;

    public StockDaoInMemory(Map<String, Stock> memory)
    {
        this.memory = memory;
    }

    @Override
    public Stock findBySymbol(String symbol)
    {
        return memory.get(symbol);
    }

    @Override
    public void saveOrUpdate(Stock stock)
    {
        memory.put(stock.getSymbol(), stock);
    }
}
