# RUN INSTRUCTIONS

`mvn spring-boot:run  -Dspring-boot.run.arguments=--stocks=${stock_file_path}`  

Pass the `${stock_file_path}` path to a csv file containing the stock set you want to work with.  
Its format must be as the one in **resources/stocks.csv** which is the default one in case you  
do not provide the `--stock` option.  

Swagger is in place, open your browser on http://localhost:8080/swagger-ui.html and walk  
through the api.  


# DISCUSSION

Looking on internet trying to figure out how and against which data the given formula should be applied,  
the first one who to discuss is probably the `P / E` ratio.  
`P / E` which mean `Price / Earning` seems to be the short name of `P / EPS` which means `Price / Earning Per Share`
which formula seems to be:  
   `EPS = (Net Income - Dividends on Preferred Stock) / Average Outstanding Shares of Common Stock`  
In principle we should then have a starting value for `Outstanding Shares of Common Stock` then incrementing  
and decrementing that number for trade of type `SELL` and `BUY` respectively then calculate the numerator  
from the trade values and the stock ones.

However the formula provided is simplified to `Market Price / Dividend` which should be interpreted to:

1. COMMON stock: `Market Price / Last dividend` in case of common stock
2. PREFERRED stock: `Market Price / Last dividend - Preferred dividend` in case of preferred stock,  
where `Preferred dividend = Fixed dividend * Par value` 

About the dividend yield, for what I could see on internet I got the idea that it should be calculated  
only with common share data.  
I assume here that the text of the exercise in case of preferred stock (who actually - as I told before - are stocks  
with both commons and preferred shares) ask for the dividend yield referred to the preferred stakeholders
(i.e. not the one usually considered) which formula is `Fixed dividend * Par value / Market price`.  


# DESIGN NOTES

About the model, I was in doubt between introducing a value object enum `StockType` to represent `COMMON` and  
`PREFERRED` stock with the draw-back of having the `parValue` field **nullable** to not be used in case  
of `COMMON` stocks, or using a base common model `Stock` with the drawback that `StockCommon` is coincident with it  
(while I did not considered at all inheriting `StockPreferred` from `StockCommon`, to give up the `Stock` base class  
, because it sounds to me strange in the context of their financial meaning).  
Finally I decided for the second one, but I still don't like it so much.  
In any case, the hypothetical database table behind the stock classes would be the same,  
with a `PAR_VALUE` nullable column, reason why someone could prefer having the model consistent with  
the underlying table.  
At the end I found that the previous choice was wrong, since to discern within `StockCommon`  
and `StockPreferred` I had to ask for the type and furthermore, I still had the need to introduce the `StockType` enum.
