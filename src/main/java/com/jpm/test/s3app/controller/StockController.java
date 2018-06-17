package com.jpm.test.s3app.controller;

import com.jpm.test.s3app.controller.exception.InvalidNumberExceptionResponse;
import com.jpm.test.s3app.controller.exception.SymbolNotFoundExceptionResponse;
import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import java.math.BigDecimal;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jpm.test.s3app.service.StockService;

@RestController
@RequestMapping("v1/stock")
public class StockController
{
    private final StockService stockService;

    @Autowired
    public StockController(final StockService stockService)
    {
        this.stockService = stockService;
    }


    @GetMapping("/{symbol}/dividend_yield")
    public BigDecimal calculateDividendYield(@PathVariable String symbol,
        @RequestParam(value="price", required=true) double marketPrice) throws SymbolNotFoundExceptionResponse, InvalidNumberExceptionResponse
    {
        try
        {
            return stockService.calculateDividendYield(symbol, marketPrice);
        }
        catch (SymbolNotFoundException ex)
        {
            throw new SymbolNotFoundExceptionResponse(symbol);
        }
        catch (InvalidNumberException ex)
        {
            throw new InvalidNumberExceptionResponse("market price", marketPrice);
        }
    }

    @GetMapping("/{symbol}/price_earning_ratio")
    public BigDecimal calculatePriceEarningRatio(@PathVariable String symbol,
        @RequestParam(value="price", required=true) double marketPrice) throws SymbolNotFoundExceptionResponse, InvalidNumberExceptionResponse
    {
        try
        {
            return stockService.calculatePriceEarningRatio(symbol, marketPrice);
        }
        catch (SymbolNotFoundException ex)
        {
            throw new SymbolNotFoundExceptionResponse(symbol);
        }
        catch (InvalidNumberException ex)
        {
            throw new InvalidNumberExceptionResponse("market price", marketPrice);
        }
    }
}

