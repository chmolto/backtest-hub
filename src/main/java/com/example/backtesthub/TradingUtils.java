package com.example.backtesthub;

public class TradingUtils{

    public static double getTakeProfitPrice(double entryPrice, double takeProfit) {
        return entryPrice * (1 + takeProfit / 100);
    }

    public static double getStopLossPrice(double entryPrice, double stopLoss) {
        return entryPrice * (1 - stopLoss / 100);
    }

}
