package com.example.backtesthub.service;

import com.example.backtesthub.models.CoinAPIRequest;
import com.example.backtesthub.models.CoinAPIResponse;
import com.example.backtesthub.strategies.TradingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CoinAPIService{

    @Value("${coinapi.apiKey}")
    // Make sure you have this property in your application.properties or application.yml file
    private String apiKey;

    private static final String BASE_URL = "https://rest.coinapi.io/v1/";

    private final WebClient.Builder webClientBuilder;

    public CoinAPIService(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;
    }

    public List<CoinAPIResponse> getHistoricalData(CoinAPIRequest request){
        String url = buildHistoricalDataUrl(request);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .header("X-CoinAPI-Key", apiKey)
                .retrieve()
                .bodyToFlux(CoinAPIResponse.class)
                .collectList()
                .block();
    }

    private String buildHistoricalDataUrl(CoinAPIRequest request){
        StringBuilder urlBuilder = new StringBuilder("ohlcv/")
                .append(request.getSymbolId())
                .append("/history")
                .append("?period_id=").append(request.getPeriodId())
                .append("&time_start=").append(request.getTimeStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if(request.getTimeEnd() != null){
            urlBuilder.append("&time_end=").append(request.getTimeEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        urlBuilder.append("&limit=").append(request.getLimit())
                .append("&include_empty_items=").append(request.isIncludeEmptyItems());

        return urlBuilder.toString();
    }

    public void simulateTrading(List<CoinAPIResponse> historicalData, TradingStrategy strategy){
        // Simulate trading based on the provided strategy
        // This is a simplified simulation and doesn't handle actual orders or transactions

        double capital = 10000.0;  // Initial trading capital
        double position = 0.0;  // Current position (number of coins held)
        double entryPrice = 0.0;  // Price at which the position was entered

        for(CoinAPIResponse currentData : historicalData){
            if(strategy.shouldBuy(currentData, historicalData)){
                // Buy
                if(position == 0.0){
                    entryPrice = currentData.getPriceClose();
                    position = capital / entryPrice;
                    System.out.println("Buy at " + currentData.getTimePeriodEnd() + ", Price: " + entryPrice);
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
                double takeProfitPrice = strategy.getTakeProfitPrice(entryPrice);
                double stopLossPrice = strategy.getStopLossPrice(entryPrice);

                if(currentData.getPriceHigh() >= takeProfitPrice){
                    // Take Profit
                    double exitPrice = takeProfitPrice;
                    double profitLoss = (exitPrice - entryPrice) * position;
                    capital += profitLoss;
                    position = 0.0;
                    System.out.println("Take Profit at " + currentData.getTimePeriodEnd() + ", Price: " + exitPrice +
                            ", Profit/Loss: " + profitLoss);
                } else if(currentData.getPriceLow() <= stopLossPrice){
                    // Stop Loss
                    double exitPrice = stopLossPrice;
                    double profitLoss = (exitPrice - entryPrice) * position;
                    capital += profitLoss;
                    position = 0.0;
                    System.out.println("Stop Loss at " + currentData.getTimePeriodEnd() + ", Price: " + exitPrice +
                            ", Profit/Loss: " + profitLoss);
                }
            }
        }

        // Print final capital
        System.out.println("Final Capital: " + capital);
    }
}