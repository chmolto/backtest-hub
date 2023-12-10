package com.example.backtesthub;

import com.example.backtesthub.models.CoinAPIRequest;
import com.example.backtesthub.models.CoinAPIResponse;
import com.example.backtesthub.service.CoinAPIService;
import com.example.backtesthub.strategies.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BacktestHubApplication{

    private static final double TAKE_PROFIT_PERCENT = 2.0;
    private static final double STOP_LOSS_PERCENT = 2.0;

    public static void main(String[] args){
        SpringApplication.run(BacktestHubApplication.class, args);
    }

    @Bean
    public CommandLineRunner executeCoinAPIService(CoinAPIService coinAPIService){
        return args -> {
            CoinAPIRequest request = new CoinAPIRequest("COINBASE_SPOT_BTC_USD", "5MIN", LocalDateTime.now().minusDays(7), LocalDateTime.now(), 1000, false);
            List<CoinAPIResponse> historicalData = coinAPIService.getHistoricalData(request);
            List<TradingStrategy> strategies = new ArrayList<>();

            strategies.add(new MovingAverageStrategy(20, 50, 1.5));
            strategies.add(new RSIStrategy());
            strategies.add(new MACDStrategy());
            strategies.add(new BollingerBandsStrategy());
            strategies.add(new MovingAverageCrossoverStrategy());

            for(TradingStrategy strategy : strategies){
                executeStrategy(strategy, historicalData);
            }
        };
    }

    public void executeStrategy(TradingStrategy strategy, List<CoinAPIResponse> historicalData){
        // Simulate trading based on the provided strategy
        // This is a simplified simulation and doesn't handle actual orders or transactions

        double capital = 10000.0;  // Initial trading capital
        double position = 0.0;     // Current position (number of coins held)
        double entryPrice = 0.0;   // Price at which the position was entered

        for(CoinAPIResponse currentData : historicalData){
            if(strategy.shouldBuy(currentData, historicalData)){
                // Buy
                if(position == 0.0){
                    entryPrice = currentData.getPriceClose();
                    position = capital / entryPrice;
                    System.out.println("Buy at " + currentData.getTimePeriodEnd() + ", Price: " + entryPrice +
                            ", Position: " + position);
                }
            } else if(strategy.shouldSell(currentData, historicalData)){
                // Sell
                if(position > 0.0){
                    double exitPrice = currentData.getPriceClose();
                    double profitLoss = (exitPrice - entryPrice) * position;
                    capital += profitLoss;
                    position = 0.0;
                    System.out.println("Sell at " + currentData.getTimePeriodEnd() + ", Price: " + exitPrice +
                            ", Profit/Loss: " + profitLoss);
                }
            }

            // Check for take-profit and stop-loss
            if(position > 0.0){
                double takeProfitPrice = TradingUtils.getTakeProfitPrice(entryPrice, TAKE_PROFIT_PERCENT);
                double stopLossPrice = TradingUtils.getStopLossPrice(entryPrice, STOP_LOSS_PERCENT);

                if(currentData.getPriceHigh() >= takeProfitPrice || currentData.getPriceLow() <= stopLossPrice){
                    // Take Profit or Stop Loss
                    double exitPrice = (currentData.getPriceHigh() >= takeProfitPrice) ? takeProfitPrice : stopLossPrice;
                    double profitLoss = (exitPrice - entryPrice) * position;
                    capital += profitLoss;
                    position = 0.0;
                    System.out.println((currentData.getPriceHigh() >= takeProfitPrice ? "Take Profit" : "Stop Loss") +
                            " at " + currentData.getTimePeriodEnd() + ", Price: " + exitPrice +
                            ", Profit/Loss: " + profitLoss);
                }
            }
        }

        // Print final capital
        System.out.println("Final Capital: " + capital);
    }

}
