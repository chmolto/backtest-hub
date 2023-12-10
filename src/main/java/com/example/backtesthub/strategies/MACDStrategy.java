package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;

import java.util.ArrayList;
import java.util.List;

public class MACDStrategy implements TradingStrategy {

    private static final int SHORT_TERM_PERIOD = 12;
    private static final int LONG_TERM_PERIOD = 26;
    private static final int SIGNAL_PERIOD = 9;

    private List<Double> shortTermEMAValues;
    private List<Double> longTermEMAValues;

    public MACDStrategy() {
        this.shortTermEMAValues = new ArrayList<>();
        this.longTermEMAValues = new ArrayList<>();
    }

    @Override
    public boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        calculateEMAs(historicalData);
        double macdLine = calculateMACDLine();
        double signalLine = calculateSignalLine();

        // Buy signal if MACD crosses above Signal Line
        return macdLine > signalLine && isCrossingAbove(macdLine, signalLine);
    }

    @Override
    public boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        calculateEMAs(historicalData);
        double macdLine = calculateMACDLine();
        double signalLine = calculateSignalLine();

        // Sell signal if MACD crosses below Signal Line
        return macdLine < signalLine && isCrossingBelow(macdLine, signalLine);
    }

    private void calculateEMAs(List<CoinAPIResponse> historicalData) {
        shortTermEMAValues.clear();
        longTermEMAValues.clear();

        for (int i = 0; i < historicalData.size(); i++) {
            if (i >= SHORT_TERM_PERIOD - 1) {
                double shortTermEMA = calculateEMA(historicalData, i, SHORT_TERM_PERIOD);
                shortTermEMAValues.add(shortTermEMA);
            }

            if (i >= LONG_TERM_PERIOD - 1) {
                double longTermEMA = calculateEMA(historicalData, i, LONG_TERM_PERIOD);
                longTermEMAValues.add(longTermEMA);
            }
        }
    }

    private double calculateEMA(List<CoinAPIResponse> historicalData, int index, int period) {
        double multiplier = 2.0 / (period + 1);
        double sum = historicalData.get(index).getPriceClose();

        for (int i = 1; i < period; i++) {
            sum += historicalData.get(index - i).getPriceClose() * Math.pow(1 - multiplier, i);
        }

        return sum * multiplier;
    }

    private double calculateMACDLine() {
        int lastIndex = shortTermEMAValues.size() - 1;
        return shortTermEMAValues.get(lastIndex) - longTermEMAValues.get(lastIndex);
    }

    private double calculateSignalLine() {
        if (shortTermEMAValues.size() >= SIGNAL_PERIOD) {
            List<Double> macdValues = shortTermEMAValues.subList(shortTermEMAValues.size() - SIGNAL_PERIOD, shortTermEMAValues.size());
            return calculateSimpleMovingAverage(macdValues);
        } else {
            return 0.0; // Default to 0 if there are not enough MACD values for the Signal Line
        }
    }

    private double calculateSimpleMovingAverage(List<Double> prices) {
        if (prices == null || prices.isEmpty()) {
            throw new IllegalArgumentException("Prices cannot be null or empty");
        }

        double sum = 0;
        for (Double price : prices) {
            sum += price;
        }

        return sum / prices.size();
    }

    private boolean isCrossingAbove(double value1, double value2) {
        return value1 > value2;
    }

    private boolean isCrossingBelow(double value1, double value2) {
        return value1 < value2;
    }
}