package com.jpm.test.s3app.dao;

import com.jpm.test.s3app.model.Trade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TradeDaoInMemory implements TradeDao
{
    private final List<Trade> memory;

    public TradeDaoInMemory(final List<Trade> memory)
    {
        this.memory = memory;
    }

    @Override
    public void create(Trade trade)
    {
        trade.setTimestamp(LocalDateTime.now());
        memory.add(trade);
    }

    @Override
    public List<Trade> findWithPredicate(Predicate<Trade> predicate)
    {
        return memory.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public List<Trade> findFromTimestampWithPredicate(LocalDateTime timestamp, Predicate<Trade> predicate)
    {
        ListIterator memoryIterator = memory.listIterator(memory.size());
        List<Trade> tradeList = new ArrayList<>();
        while(memoryIterator.hasPrevious())
        {
            Trade trade = (Trade) memoryIterator.previous();
            if (!trade.getTimestamp().isBefore(timestamp) && predicate.test(trade))
            {
                tradeList.add(0, trade);
            }
            else
            {
                break;
            }
        }
        return tradeList;
    }

    @Override
    public List<Trade> findInDate(LocalDate localDate)
    {
        int i=0;
        for (;i<memory.size(); i++)
        {
            if (!memory.get(i).getTimestamp().toLocalDate().isBefore(localDate))
            {
                break;
            }
        }
        List<Trade> tradeList = new ArrayList<>();
        for (int j=i; j<memory.size(); j++)
        {
            if (memory.get(j).getTimestamp().toLocalDate().isAfter(localDate))
            {
                break;
            }
            tradeList.add(memory.get(j));
        }
        return tradeList;
    }
}
