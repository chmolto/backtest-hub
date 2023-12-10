package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovingAverageCrossoverStrategy implements TradingStrategy {

    private static final int SHORT_TERM_PERIOD = 50;
    private static final int LONG_TERM_PERIOD = 200;

    @Override
    public boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        if (historicalData.size() < LONG_TERM_PERIOD) {
            return false; // Not enough data to make a decision
        }

        List<Double> shortTermMA = calculateMovingAverage(historicalData, SHORT_TERM_PERIOD);
        List<Double> longTermMA = calculateMovingAverage(historicalData, LONG_TERM_PERIOD);

        int lastIndex = shortTermMA.size() - 1;

        // Buy signal when the short-term moving average crosses above the long-term moving average
        return shortTermMA.get(lastIndex) > longTermMA.get(lastIndex - 1) && shortTermMA.get(lastIndex - 1) <= longTermMA.get(lastIndex - 2);
    }

    @Override
    public boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        if (historicalData.size() < LONG_TERM_PERIOD) {
            return false; // Not enough data to make a decision
        }

        List<Double> shortTermMA = calculateMovingAverage(historicalData, SHORT_TERM_PERIOD);
        List<Double> longTermMA = calculateMovingAverage(historicalData, LONG_TERM_PERIOD);

        int lastIndex = shortTermMA.size() - 1;

        // Sell signal when the short-term moving average crosses below the long-term moving average
        return shortTermMA.get(lastIndex) < longTermMA.get(lastIndex - 1) && shortTermMA.get(lastIndex - 1) >= longTermMA.get(lastIndex - 2);
    }

    private List<Double> calculateMovingAverage(List<CoinAPIResponse> data, int period) {
        if (data.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate moving average");
        }

        List<Double> closingPrices = extractClosingPrices(data);

        // Calculate simple moving average
        return calculateSimpleMovingAverage(closingPrices, period);
    }

    public static List<Double> calculateSimpleMovingAverage(List<Double> data, int period) {
        if (data == null || data.isEmpty() || period <= 0) {
            throw new IllegalArgumentException("Invalid input for calculating simple moving average");
        }

        List<Double> movingAverages = new ArrayList<>();

        for (int i = period - 1; i < data.size(); i++) {
            double sum = 0;
            for (int j = 0; j < period; j++) {
                sum += data.get(i - j);
            }
            movingAverages.add(sum / period);
        }

        return Collections.unmodifiableList(movingAverages);
    }

    private List<Double> extractClosingPrices(List<CoinAPIResponse> data) {
        return data.stream().map(CoinAPIResponse::getPriceClose).toList();
    }
}