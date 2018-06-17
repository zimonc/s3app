package com.jpm.test.s3app;

import com.jpm.test.s3app.dao.StockDao;
import com.jpm.test.s3app.dao.StockDaoInMemory;
import com.jpm.test.s3app.dao.TradeDao;
import com.jpm.test.s3app.dao.TradeDaoInMemory;
import com.jpm.test.s3app.model.Stock;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.Collections;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class S3Application implements WebMvcConfigurer
{

    public static void main(String[] args)
    {
        SpringApplication.run(S3Application.class, args);
    }

    @Bean
    public StockDao stockDao()
    {
        return new StockDaoInMemory(new HashMap<String, Stock>());
    }

    @Bean
    public TradeDao tradeDao()
    {
        return new TradeDaoInMemory(new ArrayList<>());
    }

    private MathContext mathContext()
    {
        return new MathContext(3, RoundingMode.HALF_UP);
    }

    @Bean
    public Docket docket()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(getClass().getPackage().getName()))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(generateApiInfo());
    }

    private ApiInfo generateApiInfo()
    {
        return new ApiInfo(
            "Super Simple Stock Application",
            "JP Morgan notorious test",
            "1.0-SNAPSHOT",
            "urn:tos",
            new Contact("Candidate", "http:\\www.simone.com", "simone@server.com"),
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            new ArrayList<VendorExtension>());
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry)
//    {
//        registry.addInterceptor(new LoggingInterceptor()).addPathPatterns("/**");
//    }
//
//
}
