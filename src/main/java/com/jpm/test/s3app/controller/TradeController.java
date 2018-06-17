package com.jpm.test.s3app.controller;

import com.jpm.test.s3app.controller.exception.InvalidNumberExceptionResponse;
import com.jpm.test.s3app.controller.exception.SymbolNotFoundExceptionResponse;
import com.jpm.test.s3app.model.Trade;
import com.jpm.test.s3app.service.TradeService;
import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/trade")
public class TradeController
{
    private final TemporalAmount VWSP_PERIOD_BACK = Period.ofDays(15);
    private final TradeService tradeService;

    @Autowired
    public TradeController(final TradeService tradeService)
    {
        this.tradeService = tradeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TradeDto create(@Valid @RequestBody TradeDto tradeDto) throws SymbolNotFoundExceptionResponse, InvalidNumberExceptionResponse
    {
        try
        {
            Trade trade = tradeService.create(tradeDto);
            tradeDto.setTimestamp(trade.getTimestamp());
            return tradeDto;
        }
        catch (SymbolNotFoundException e)
        {
            throw new SymbolNotFoundExceptionResponse(tradeDto.getSymbol());
        }
        catch (InvalidNumberException e)
        {
            throw new InvalidNumberExceptionResponse(e.getMessage());
        }
    }

    @GetMapping("{symbol}")
    public BigDecimal calculateVolumeWeightedStockPrice(@PathVariable String symbol) throws SymbolNotFoundExceptionResponse
    {
        LocalDateTime timestamp = LocalDateTime.now().minus(VWSP_PERIOD_BACK);
        try {
            return tradeService.calculateVolumeWeightedStockPrice(symbol, timestamp);
        }
        catch (SymbolNotFoundException e)
        {
            throw new SymbolNotFoundExceptionResponse(symbol);
        }
    }

    @GetMapping
    public BigDecimal calculateAllShareIndex(
        @RequestParam(value="date", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate)
    {
        if (localDate == null)
            localDate = LocalDate.now();
        return tradeService.calculateAllShareIndex(localDate);
    }
}
