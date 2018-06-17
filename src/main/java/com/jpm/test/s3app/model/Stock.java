package com.jpm.test.s3app.model;

import com.jpm.test.s3app.model.vo.StockType;

public class Stock
{
    private String symbol;
    private double lastDividend;
    private double parValue;


    public Stock(String symbol, double lastDividend, double parValue)
    {
        this.symbol = symbol;
        this.lastDividend = lastDividend;
        this.parValue = parValue;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public double getLastDividend()
    {
        return lastDividend;
    }

    public double getParValue()
    {
        return parValue;
    }
}
