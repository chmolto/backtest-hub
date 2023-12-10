package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;

import java.util.List;

public class MovingAverageStrategy implements TradingStrategy{

    private int shortTermPeriod;
    private int longTermPeriod;
    private double riskPerTrade;  // A fixed percentage of trading capital to risk per trade

    public MovingAverageStrategy(int shortTermPeriod, int longTermPeriod,
                                 double riskPerTrade){
        this.shortTermPeriod = shortTermPeriod;
        this.longTermPeriod = longTermPeriod;
        this.riskPerTrade = riskPerTrade;
    }

    @Override
    public boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData){
        if(historicalData.size() < longTermPeriod){
            // Not enough data to make a decision
            return false;
        }

        double shortTermSMA = calculateSMA(historicalData.subList(0, shortTermPeriod));
        double longTermSMA = calculateSMA(historicalData.subList(0, longTermPeriod));

        // Buy if short-term SMA is above long-term SMA and the risk is acceptable
        return shortTermSMA > longTermSMA && calculateRisk(currentData, historicalData) <= riskPerTrade;
    }

    @Override
    public boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData){
        if(historicalData.size() < longTermPeriod){
            // Not enough data to make a decision
            return false;
        }

        double shortTermSMA = calculateSMA(historicalData.subList(0, shortTermPeriod));
        double longTermSMA = calculateSMA(historicalData.subList(0, longTermPeriod));

        // Sell if short-term SMA is below long-term SMA or if the risk exceeds the threshold
        // Also, consider implementing take-profit and stop-loss orders
        return shortTermSMA < longTermSMA || calculateRisk(currentData, historicalData) > riskPerTrade;
    }

    private double calculateSMA(List<CoinAPIResponse> data){
        double sum = 0;

        for(CoinAPIResponse response : data){
            sum += response.getPriceClose();
        }

        return sum / data.size();
    }

    private double calculateRisk(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData){
        // Calculate the risk for the next trade based on historical data and current market conditions
        double averageTrueRange = calculateAverageTrueRange(historicalData);
        double currentPrice = currentData.getPriceClose();
        double riskPerShare = averageTrueRange * currentPrice;

        // Assuming a fixed percentage of the trading capital is at risk for each trade
        return riskPerShare / currentData.getPriceClose() * 100;
    }

    private double calculateAverageTrueRange(List<CoinAPIResponse> historicalData){
        // Calculate the Average True Range (ATR)
        double sum = 0;

        for(int i = 1; i < historicalData.size(); i++){
            double highLowRange = Math.abs(historicalData.get(i).getPriceHigh() - historicalData.get(i).getPriceLow());
            double highCloseRange = Math.abs(historicalData.get(i).getPriceHigh() - historicalData.get(i - 1).getPriceClose());
            double lowCloseRange = Math.abs(historicalData.get(i).getPriceLow() - historicalData.get(i - 1).getPriceClose());

            sum += Math.max(highLowRange, Math.max(highCloseRange, lowCloseRange));
        }

        return sum / historicalData.size();
    }

}
