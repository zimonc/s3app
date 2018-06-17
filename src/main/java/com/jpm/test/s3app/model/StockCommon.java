package com.jpm.test.s3app.model;

public class StockCommon extends Stock
{
    private double parValue;

    public StockCommon(String symbol, double lastDividend, double parValue)
    {
        super(symbol, lastDividend, parValue);
        this.parValue = parValue;
    }
}
