package com.jpm.test.s3app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpm.test.s3app.model.StockPreferred;
import com.jpm.test.s3app.model.Trade;
import com.jpm.test.s3app.model.vo.TradeType;
import com.jpm.test.s3app.service.TradeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(TradeController.class)
public class TradeControllerTest
{
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeService tradeService;

    @Test
    public void create() throws Exception
    {
        TradeDto tradeDto = new TradeDto("POP", TradeType.BUY, 2, 18.1);
        Trade trade = new Trade(new StockPreferred(tradeDto.getSymbol(), 8, 2, 70),
            tradeDto.getType(), tradeDto.getQuantity(), tradeDto.getPrice());
        trade.setTimestamp(LocalDateTime.now());
        Mockito.when(tradeService.create(Mockito.isA(TradeDto.class))).thenReturn(trade);

        String timestamp = trade.getTimestamp().format(DateTimeFormatter.ofPattern(TradeDto.DATE_FORMAT));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/trade")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(tradeDto)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.symbol", Matchers.equalTo(tradeDto.getSymbol())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type", Matchers.equalTo(tradeDto.getType().name())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.quantity", Matchers.equalTo(tradeDto.getQuantity())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.price", Matchers.equalTo(tradeDto.getPrice())))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", Matchers.equalTo(timestamp)))
            .andReturn();

        Mockito.verify(tradeService, Mockito.times(1)).create(Mockito.isA(TradeDto.class));
    }

    @Test
    public void calculateVolumeWeightedStockPrice() throws Exception
    {
        String symbol = "symbol";

        Mockito.when(tradeService.calculateVolumeWeightedStockPrice(Mockito.isA(String.class), Mockito.isA(LocalDateTime.class)))
            .thenReturn(BigDecimal.valueOf(12.3));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/v1/trade/%s", symbol)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("12.3"))
            .andReturn().getResponse();

        Mockito.verify(tradeService, Mockito.times(1)).calculateVolumeWeightedStockPrice(Mockito.isA(String.class), Mockito.isA(LocalDateTime.class));
    }

    @Test
    public void calculateVolumeWeightedStockPriceNoTrades() throws Exception
    {
        String symbol = "symbol";

        Mockito.when(tradeService.calculateVolumeWeightedStockPrice(Mockito.isA(String.class), Mockito.isA(LocalDateTime.class)))
            .thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/v1/trade/%s", symbol)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(""))
            .andReturn();

        Mockito.verify(tradeService, Mockito.times(1)).calculateVolumeWeightedStockPrice(Mockito.isA(String.class), Mockito.isA(LocalDateTime.class));
    }

    @Test
    public void calculateAllShareIndex() throws Exception
    {
        LocalDate localDate = LocalDate.of(2018, 3, 12);
        Mockito.when(tradeService.calculateAllShareIndex(localDate))
            .thenReturn(BigDecimal.valueOf(123.3));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/trade?date=2018-03-12"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("123.3"))
            .andReturn();

        ArgumentCaptor<LocalDate> argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        Mockito.verify(tradeService, Mockito.times(1)).calculateAllShareIndex(argumentCaptor.capture());
        Assert.assertEquals(localDate, argumentCaptor.getValue());
    }

    @Test
    public void calculateAllShareIndexDefaultDate() throws Exception
    {
        LocalDate localDate = LocalDate.now();
        Mockito.when(tradeService.calculateAllShareIndex(localDate))
            .thenReturn(BigDecimal.valueOf(123.3));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/trade"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("123.3"))
            .andReturn();

        ArgumentCaptor<LocalDate> argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        Mockito.verify(tradeService, Mockito.times(1)).calculateAllShareIndex(argumentCaptor.capture());
        Assert.assertEquals(localDate, argumentCaptor.getValue());
    }
}