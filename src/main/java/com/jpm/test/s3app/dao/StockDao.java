package com.jpm.test.s3app.dao;

import com.jpm.test.s3app.model.Stock;

public interface StockDao
{
    Stock findBySymbol(String symbol);

    void saveOrUpdate(Stock stock);
}
