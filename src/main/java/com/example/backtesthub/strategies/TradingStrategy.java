package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;

import java.util.List;

public interface TradingStrategy {
    boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData);
    boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData);

    double getTakeProfitPrice(double entryPrice);
    double getStopLossPrice(double entryPrice);
}