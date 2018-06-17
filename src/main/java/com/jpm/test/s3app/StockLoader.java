package com.jpm.test.s3app;

import com.jpm.test.s3app.dao.StockDao;
import com.jpm.test.s3app.util.StockFactory;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StockLoader implements ApplicationRunner
{
    private static final Logger logger = LoggerFactory.getLogger(StockLoader.class);
    private StockDao stockDao;
    private static String STOCK_FILE_ARG = "stocks";

    @Autowired
    public StockLoader(StockDao stockDao)
    {
        this.stockDao = stockDao;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        File stocks;
        if (args.containsOption(STOCK_FILE_ARG))
        {
            List<String> stocksOptions = args.getOptionValues(STOCK_FILE_ARG);
            if (stocksOptions.size() > 1)
            {
                throw new Exception("Only one stock file data must be passed");
            }
            stocks = new File(stocksOptions.get(0));
            if (!stocks.exists())
            {
                throw new Exception(String.format("Stock file %s doesn't exist", stocks.getPath()));
            }
        }
        else
        {
            stocks = new File(this.getClass().getClassLoader()
                .getResource(String.format("%s.csv", STOCK_FILE_ARG)).getFile());
        }
        logger.debug(String.format("Using %s stocks file", stocks.getPath()));
        load(stocks);
    }


    private void load(File stocks) throws IOException
    {
        try (
            Reader reader = Files.newBufferedReader(Paths.get(stocks.getPath()));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withHeader("SYMBOL", "TYPE", "LAST_DIVIDEND", "FIXED_DIVIDEND", "PAR_VALUE")
                .withIgnoreHeaderCase()
                .withTrim()
                .withSkipHeaderRecord());
        ) {
            for (CSVRecord csvRecord : csvParser) {
                Map<String, String> map = csvRecord.toMap();
                logger.debug(String.format("Loading stock: %s", map.entrySet().stream()
                    .map(e -> String.format("%s=%s", e.getKey() , e.getValue())).collect(Collectors.joining(", "))));
                stockDao.saveOrUpdate(StockFactory.fromMap(map));
            }
        }
    }
}
