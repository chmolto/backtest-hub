package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;

import java.util.List;

public class RSIStrategy implements TradingStrategy {

    private static final int PERIOD = 14; // RSI period, you can adjust this based on your preference
    private static final int OVERSOLD_THRESHOLD = 30;
    private static final int OVERBOUGHT_THRESHOLD = 70;

    @Override
    public boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        if (historicalData.size() < PERIOD) {
            // Not enough data to calculate RSI
            return false;
        }

        double rsi = calculateRSI(historicalData);
        return rsi < OVERSOLD_THRESHOLD;
    }

    @Override
    public boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        if (historicalData.size() < PERIOD) {
            // Not enough data to calculate RSI
            return false;
        }

        double rsi = calculateRSI(historicalData);
        return rsi > OVERBOUGHT_THRESHOLD;
    }

    private double calculateRSI(List<CoinAPIResponse> historicalData) {
        int dataSize = historicalData.size();

        // Calculate average gain and average loss
        double avgGain = 0.0;
        double avgLoss = 0.0;

        for (int i = 1; i < dataSize; i++) {
            double priceDiff = historicalData.get(i).getPriceClose() - historicalData.get(i - 1).getPriceClose();

            if (priceDiff > 0) {
                avgGain += priceDiff;
            } else {
                avgLoss += Math.abs(priceDiff);
            }
        }

        avgGain /= PERIOD;
        avgLoss /= PERIOD;

        // Calculate relative strength (RS)
        double rs = (avgGain == 0) ? 0 : avgGain / avgLoss;

        // Calculate RSI
        double rsi = 100 - (100 / (1 + rs));

        return rsi;
    }
}
