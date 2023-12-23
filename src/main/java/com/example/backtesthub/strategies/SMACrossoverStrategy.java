package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
public class SMACrossoverStrategy implements TradingStrategy {
    private int shortTermPeriod;
    private int longTermPeriod;

    public SMACrossoverStrategy(int shortTermPeriod, int longTermPeriod) {
        this.shortTermPeriod = shortTermPeriod;
        this.longTermPeriod = longTermPeriod;
    }

    @Override
    public boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        MovingAverage shortTermMA = new MovingAverage(shortTermPeriod);
        MovingAverage longTermMA = new MovingAverage(longTermPeriod);

        for (int i = 0; i < historicalData.size(); i++) {
            CoinAPIResponse dataPoint = historicalData.get(i);
            shortTermMA.add(dataPoint.getPriceClose());
            longTermMA.add(dataPoint.getPriceClose());

            if (dataPoint.equals(currentData)) {
                double previousShortMA = shortTermMA.getPreviousAverage();
                double previousLongMA = longTermMA.getPreviousAverage();

                // Check if the short-term MA has just crossed above the long-term MA
                if (shortTermMA.getCurrentAverage() > longTermMA.getCurrentAverage() && previousShortMA <= previousLongMA) {
                    return true;
                }
                break;
            }
        }

        return false;
    }

    @Override
    public boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        MovingAverage shortTermMA = new MovingAverage(shortTermPeriod);
        MovingAverage longTermMA = new MovingAverage(longTermPeriod);

        for (int i = 0; i < historicalData.size(); i++) {
            CoinAPIResponse dataPoint = historicalData.get(i);
            shortTermMA.add(dataPoint.getPriceClose());
            longTermMA.add(dataPoint.getPriceClose());

            if (dataPoint.equals(currentData)) {
                double previousShortMA = shortTermMA.getPreviousAverage();
                double previousLongMA = longTermMA.getPreviousAverage();

                // Check if the short-term MA has just crossed below the long-term MA
                if (shortTermMA.getCurrentAverage() < longTermMA.getCurrentAverage() && previousShortMA >= previousLongMA) {
                    return true;
                }
                break;
            }
        }

        return false;
    }

    private class MovingAverage {
        private final int period;
        private final Queue<Double> window = new LinkedList<>();
        private double sum = 0.0;
        private double previousAverage = 0.0;

        public MovingAverage(int period) {
            this.period = period;
        }

        public void add(double num) {
            sum += num;
            window.add(num);
            if (window.size() > period) {
                sum -= window.remove();
            }
            previousAverage = getCurrentAverage();
        }

        public double getCurrentAverage() {
            if (window.isEmpty()) return 0; // or throw exception
            return sum / window.size();
        }

        public double getPreviousAverage() {
            return previousAverage;
        }
    }
}
