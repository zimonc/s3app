package com.jpm.test.s3app.model;

import com.jpm.test.s3app.model.vo.TradeType;
import java.time.LocalDateTime;

public class Trade
{
    private LocalDateTime timestamp;
    private Stock stock;
    private TradeType type;
    private int quantity;
    private double price;

    public Trade(Stock stock, TradeType type, int quantity, double price)
    {
        this.timestamp = null;
        this.stock = stock;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
    }

    public Stock getStock()
    {
        return stock;
    }

    public TradeType getType()
    {
        return type;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public double getPrice()
    {
        return price;
    }
}
