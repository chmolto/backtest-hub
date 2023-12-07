package com.example.backtesthub;

import com.example.backtesthub.models.CoinAPIRequest;
import com.example.backtesthub.models.CoinAPIResponse;
import com.example.backtesthub.service.CoinAPIService;
import com.example.backtesthub.strategies.MovingAverageStrategy;
import com.example.backtesthub.strategies.TradingStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BacktestHubApplication{

    public static void main(String[] args) {
        SpringApplication.run(BacktestHubApplication.class, args);
    }

    @Bean
    public CommandLineRunner executeCoinAPIService(CoinAPIService coinAPIService) {
        return args -> {
            CoinAPIRequest request = new CoinAPIRequest("BTC", "5MIN", LocalDateTime.now().minusDays(7), LocalDateTime.now(), 0, true);
            List<CoinAPIResponse> historicalData = coinAPIService.getHistoricalData(request);
            List<TradingStrategy> strategies = new ArrayList<>();

            strategies.add(new MovingAverageStrategy(20, 50, 1.5, 0.02, 0.02));

            for (TradingStrategy strategy : strategies) {
                executeStrategy(strategy, historicalData);
            }
        };
    }

    private void executeStrategy(TradingStrategy strategy, List<CoinAPIResponse> historicalData) {
        // Example: Apply the strategy to historical data
        System.out.println("Executing strategy: " + strategy.getClass().getSimpleName());

        // Assuming your strategy has methods like shouldBuy and shouldSell
        for (CoinAPIResponse dataPoint : historicalData) {
            if (strategy.shouldBuy(dataPoint, historicalData)) {
                System.out.println("Buy signal at " + dataPoint.getTimePeriodEnd());
                // Additional buy logic can be added here
            } else if (strategy.shouldSell(dataPoint, historicalData)) {
                System.out.println("Sell signal at " + dataPoint.getTimePeriodEnd());
                // Additional sell logic can be added here
            }
        }

    }

}
