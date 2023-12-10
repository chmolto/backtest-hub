package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;

import java.util.List;

public class BollingerBandsStrategy implements TradingStrategy {

    private static final int PERIOD = 20;
    private static final double STANDARD_DEVIATION_MULTIPLIER = 2.0;

    @Override
    public boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        if (historicalData.size() < PERIOD) {
            return false; // Not enough data to make a decision
        }

        List<Double> closingPrices = extractClosingPrices(historicalData);
        double currentPrice = currentData.getPriceClose();

        double sma = calculateSimpleMovingAverage(closingPrices);
        double standardDeviation = calculateStandardDeviation(closingPrices, sma);

        double upperBand = sma + STANDARD_DEVIATION_MULTIPLIER * standardDeviation;

        return currentPrice < sma && currentPrice < upperBand; // Buy signal when the current price is below the upper Bollinger Band
    }

    @Override
    public boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        if (historicalData.size() < PERIOD) {
            return false; // Not enough data to make a decision
        }

        List<Double> closingPrices = extractClosingPrices(historicalData);
        double currentPrice = currentData.getPriceClose();

        double sma = calculateSimpleMovingAverage(closingPrices);
        double standardDeviation = calculateStandardDeviation(closingPrices, sma);

        double lowerBand = sma - STANDARD_DEVIATION_MULTIPLIER * standardDeviation;

        return currentPrice > sma && currentPrice > lowerBand; // Sell signal when the current price is above the lower Bollinger Band
    }

    private List<Double> extractClosingPrices(List<CoinAPIResponse> data) {
        return data.stream().map(CoinAPIResponse::getPriceClose).toList();
    }

    private double calculateSimpleMovingAverage(List<Double> prices) {
        double sum = prices.stream().mapToDouble(Double::doubleValue).sum();
        return sum / prices.size();
    }

    private double calculateStandardDeviation(List<Double> prices, double sma) {
        double sumSquaredDiff = prices.stream().mapToDouble(price -> Math.pow(price - sma, 2)).sum();
        return Math.sqrt(sumSquaredDiff / prices.size());
    }
}