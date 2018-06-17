package com.jpm.test.s3app.model;

public class StockPreferred extends Stock
{
    private double fixedDividend;

    public StockPreferred(String symbol, double lastDividend, double fixedDividend, double parValue)
    {
        super(symbol, lastDividend, parValue);
        this.fixedDividend = fixedDividend;
    }


    public double getFixedDividend()
    {
        return fixedDividend;
    }
}
