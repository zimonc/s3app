package com.jpm.test.s3app.dao;

import com.jpm.test.s3app.model.Trade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public interface TradeDao
{
    void create(Trade trade);
    List<Trade> findWithPredicate(Predicate<Trade> predicate);
    List<Trade> findInDate(LocalDate localDate);
    List<Trade> findFromTimestampWithPredicate(LocalDateTime timestamp, Predicate<Trade> predicate);
}
