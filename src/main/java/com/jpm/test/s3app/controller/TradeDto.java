package com.jpm.test.s3app.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jpm.test.s3app.model.vo.TradeType;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeDto
{
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS[XXX]";

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern=DATE_FORMAT)
    private LocalDateTime timestamp;
    @NotNull
    private String symbol;
    @NotNull
    private TradeType type;
    @NotNull
    private int quantity;
    @NotNull
    private double price;

    public TradeDto()
    {

    }

    public TradeDto(String symbol, TradeType type, int quantity, double price)
    {
        this.timestamp = null;
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    public TradeDto(LocalDateTime timestamp, String symbol, TradeType type, int quantity, double price)
    {
        this.timestamp = timestamp;
        this.symbol = symbol;
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

    public String getSymbol()
    {
        return symbol;
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
