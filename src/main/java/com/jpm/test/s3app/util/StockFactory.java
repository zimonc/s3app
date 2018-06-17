package com.jpm.test.s3app.util;

import com.jpm.test.s3app.model.Stock;
import com.jpm.test.s3app.model.StockCommon;
import com.jpm.test.s3app.model.StockPreferred;
import com.jpm.test.s3app.model.vo.StockType;
import java.util.Map;

public class StockFactory
{
    public static Stock fromMap(Map<String, String> map)
    {
        String type = map.get("TYPE"),
            symbol = map.get("SYMBOL"),
            lastDividend = map.get("LAST_DIVIDEND"),
            parValue = map.get("PAR_VALUE"),
            fixedDividend = map.get("FIXED_DIVIDEND");
        switch (StockType.valueOf(type))
        {
            case COMMON:
                return new StockCommon(symbol, Double.valueOf(lastDividend).doubleValue(), Double.valueOf(parValue).doubleValue());
            case PREFERRED:
                return new StockPreferred(symbol, Double.valueOf(lastDividend).doubleValue(), Double.valueOf(fixedDividend).doubleValue(),
                    Double.valueOf(parValue).doubleValue());
        }
        return null;
    }
}
