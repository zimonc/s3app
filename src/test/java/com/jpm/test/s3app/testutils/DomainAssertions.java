package com.jpm.test.s3app.testutils;

import com.jpm.test.s3app.model.Stock;
import com.jpm.test.s3app.model.StockCommon;
import com.jpm.test.s3app.model.StockPreferred;
import com.jpm.test.s3app.model.Trade;
import org.junit.Assert;

public class DomainAssertions
{
    private static void assertEquals(Stock expected, Stock actual)
    {
        Assert.assertEquals(expected.getClass(), actual.getClass());
        Assert.assertEquals(expected.getSymbol(), actual.getSymbol());
        Assert.assertEquals(expected.getLastDividend(), actual.getLastDividend(), 0);
        Assert.assertEquals(expected.getSymbol(), actual.getSymbol());
    }

    public static void assertEquals(StockCommon expected, StockCommon actual)
    {
        assertEquals((Stock) expected, (Stock) actual);
    }

    public static void assertEquals(StockPreferred expected, StockPreferred actual)
    {
        assertEquals((Stock) expected, (Stock) actual);
        Assert.assertEquals(expected.getFixedDividend(), actual.getFixedDividend(), 0);
    }

    public static void assertEquals(Trade expected, Trade actual)
    {
        Assert.assertTrue(expected != null);
        Assert.assertTrue(actual != null);
        if (expected.getStock().getClass().isAssignableFrom(StockCommon.class) &&
            actual.getStock().getClass().isAssignableFrom(StockCommon.class))
        {
            assertEquals((StockCommon) expected.getStock(), (StockCommon) actual.getStock());
        } else if  (expected.getStock().getClass().isAssignableFrom(StockPreferred.class) &&
            actual.getStock().getClass().isAssignableFrom(StockPreferred.class))
        {
            assertEquals((StockPreferred) expected.getStock(), (StockPreferred) actual.getStock());
        }
        else
        {
            throw new AssertionError(
                String.format("Expecting %s while found %s", expected.getClass().getName(), actual.getClass().getName()));
        }
    }
}
