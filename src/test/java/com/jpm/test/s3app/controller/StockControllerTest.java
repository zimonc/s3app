package com.jpm.test.s3app.controller;

import com.jpm.test.s3app.service.exception.InvalidNumberException;
import com.jpm.test.s3app.service.StockService;
import com.jpm.test.s3app.service.exception.SymbolNotFoundException;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(StockController.class)
public class StockControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService carServiceMock;

    // DIVIDEND YIELD TESTS

    @Test
    public void calculateDividendYield() throws Exception
    {
        String symbol = "POP";
        double price = 2.3;
        Mockito.when(carServiceMock.calculateDividendYield(symbol, price)).thenReturn(new BigDecimal(3.2));

        mockMvc.perform(MockMvcRequestBuilders.get(
            String.format("/v1/stock/%s/dividend_yield?price=%s", symbol, price)))
            .andExpect(MockMvcResultMatchers.status().isOk()).
            andReturn();

        Mockito.verify(carServiceMock, Mockito.times(1)).calculateDividendYield(symbol, price);
    }

    @Test
    public void calculateDividendYieldWithInvalidSymbol() throws Exception
    {
        String symbol = "POP";
        double price = 2.3;
        Mockito.when(carServiceMock.calculateDividendYield(symbol, price))
            .thenThrow(new SymbolNotFoundException("XXX"));

        mockMvc.perform(MockMvcRequestBuilders.get(
            String.format("/v1/stock/%s/dividend_yield?price=%s", symbol, price)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.status().reason("Could not find symbol"));

        Mockito.verify(carServiceMock, Mockito.times(1)).calculateDividendYield(symbol, price);
    }

    @Test
    public void calculateDividendYieldWithInvalidNumber() throws Exception
    {
        String symbol = "POP";
        double price = 2.3;
        Mockito.when(carServiceMock.calculateDividendYield(symbol, price))
            .thenThrow(new InvalidNumberException("XXX"));

        mockMvc.perform(MockMvcRequestBuilders.get(
            String.format("/v1/stock/%s/dividend_yield?price=%s", symbol, price)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.status().reason("Invalid number"));

        Mockito.verify(carServiceMock, Mockito.times(1)).calculateDividendYield(symbol, price);
    }

    // PRICE EARNING RATIO TESTS

    @Test
    public void calculatePriceEarningRatio() throws Exception
    {
        String symbol = "POP";
        double price = 2.3;
        Mockito.when(carServiceMock.calculatePriceEarningRatio(symbol, price)).thenReturn(new BigDecimal(3.2));

        mockMvc.perform(MockMvcRequestBuilders.get(
            String.format("/v1/stock/%s/price_earning_ratio?price=%s", symbol, price)))
            .andExpect(MockMvcResultMatchers.status().isOk()).
            andReturn();

        Mockito.verify(carServiceMock, Mockito.times(1)).calculatePriceEarningRatio(symbol, price);
    }

    @Test
    public void calculatePriceEarningRatioWithInvalidSymbol() throws Exception
    {
        String symbol = "POP";
        double price = 2.3;
        Mockito.when(carServiceMock.calculatePriceEarningRatio(symbol, price))
            .thenThrow(new SymbolNotFoundException("XXX"));

        mockMvc.perform(MockMvcRequestBuilders.get(
            String.format("/v1/stock/%s/price_earning_ratio?price=%s", symbol, price)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.status().reason("Could not find symbol"));

        Mockito.verify(carServiceMock, Mockito.times(1)).calculatePriceEarningRatio(symbol, price);
    }

    @Test
    public void calculatePriceEarningRatioWithInvalidNumber() throws Exception
    {
        String symbol = "POP";
        double price = 2.3;
        Mockito.when(carServiceMock.calculatePriceEarningRatio(symbol, price))
            .thenThrow(new InvalidNumberException("XXX"));

        mockMvc.perform(MockMvcRequestBuilders.get(
            String.format("/v1/stock/%s/price_earning_ratio?price=%s", symbol, price)))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.status().reason("Invalid number"));

        Mockito.verify(carServiceMock, Mockito.times(1)).calculatePriceEarningRatio(symbol, price);
    }
}
