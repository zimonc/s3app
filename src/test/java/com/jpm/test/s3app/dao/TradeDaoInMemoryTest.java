package com.jpm.test.s3app.dao;

import com.jpm.test.s3app.model.Trade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class TradeDaoInMemoryTest
{
    @Test
    public void save() throws Exception
    {
        List<Trade> memoryMock = Mockito.mock(List.class);
        TradeDao tradDao = new TradeDaoInMemory(memoryMock);
        Trade tradeMock = Mockito.mock(Trade.class);

        tradDao.create(tradeMock);

        Mockito.verify(tradeMock, Mockito.times(1)).setTimestamp(Mockito.isA(LocalDateTime.class));
        Mockito.verify(memoryMock, Mockito.times(1)).add(tradeMock);
    }

    @Test
    public void filterWithPredicate() throws Exception
    {
        List<Trade> memoryOfMocks = Arrays.asList(Mockito.mock(Trade.class), Mockito.mock(Trade.class));
        TradeDao tradeDao = new TradeDaoInMemory(memoryOfMocks);
        Predicate<Trade> predicateMock = Mockito.mock(Predicate.class);
        Mockito.when(predicateMock.test(Mockito.isA(Trade.class))).thenReturn(Boolean.TRUE);

        List<Trade> trades = tradeDao.findWithPredicate(predicateMock);

        Assert.assertNotNull(trades);
        Assert.assertArrayEquals(trades.toArray(), memoryOfMocks.toArray());
        ArgumentCaptor<Trade> tradeArgumentCaptor = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(predicateMock, Mockito.times(2))
            .test(tradeArgumentCaptor.capture());
        Assert.assertArrayEquals(tradeArgumentCaptor.getAllValues().toArray(), memoryOfMocks.toArray());
    }

    @Test
    public void findFromTimestampWithPredicate() throws Exception
    {
        Trade trade1 = new Trade(null, null, 0, 0);
        Trade trade2 = new Trade(null, null, 0, 0);
        Trade trade3 = new Trade(null, null, 0, 0);
        LocalDateTime timestamp = LocalDateTime.of(2018, 4, 12, 6, 22, 45, 124);
        trade1.setTimestamp(timestamp);
        trade2.setTimestamp(timestamp.plusHours(1));
        trade3.setTimestamp(timestamp.plusHours(2));
        List<Trade> memory = Arrays.asList(trade1, trade2, trade3);
        TradeDao tradeDao = new TradeDaoInMemory(memory);
        Predicate<Trade> predicateMock = Mockito.mock(Predicate.class);
        Mockito.when(predicateMock.test(Mockito.isA(Trade.class))).thenReturn(Boolean.TRUE);

        List<Trade> tradeListActual = tradeDao.findFromTimestampWithPredicate(timestamp.plusSeconds(1), predicateMock);

        List<Trade> memorySublist = memory.subList(1, memory.size());
        Assert.assertNotNull(tradeListActual);
        Assert.assertArrayEquals(tradeListActual.toArray(), memorySublist.toArray());
        ArgumentCaptor<Trade> tradeArgumentCaptor = ArgumentCaptor.forClass(Trade.class);
        Mockito.verify(predicateMock, Mockito.times(2))
            .test(tradeArgumentCaptor.capture());
        Collections.reverse(memorySublist);
        Assert.assertArrayEquals(tradeArgumentCaptor.getAllValues().toArray(), memorySublist.toArray());
    }

    @Test
    public void findInDate()
    {
        Trade trade1 = new Trade(null, null, 0, 0);
        Trade trade2 = new Trade(null, null, 0, 0);
        Trade trade3 = new Trade(null, null, 0, 0);
        LocalDateTime timestamp = LocalDateTime.now();
        trade1.setTimestamp(timestamp);
        trade2.setTimestamp(timestamp.plusDays(1));
        trade3.setTimestamp(timestamp.plusDays(2));
        List<Trade> memory = Arrays.asList(trade1, trade2, trade3);
        TradeDao tradeDao = new TradeDaoInMemory(memory);

        List<Trade> tradeList = tradeDao.findInDate(timestamp.toLocalDate());

        Assert.assertNotNull(tradeList);
        Assert.assertEquals(1, tradeList.size());
        Assert.assertEquals(trade1, tradeList.get(0));

        tradeList = tradeDao.findInDate(timestamp.plusDays(1).toLocalDate());

        Assert.assertNotNull(tradeList);
        Assert.assertEquals(1, tradeList.size());
        Assert.assertEquals(trade2, tradeList.get(0));

        tradeList = tradeDao.findInDate(timestamp.plusDays(2).toLocalDate());

        Assert.assertNotNull(tradeList);
        Assert.assertEquals(1, tradeList.size());
        Assert.assertEquals(trade3, tradeList.get(0));
    }
}