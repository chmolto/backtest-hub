package com.example.backtesthub.strategies;

import com.example.backtesthub.models.CoinAPIResponse;

import java.util.List;

public class CombinedFactorStrategy implements TradingStrategy {

    private static final int SHORT_MA_PERIOD = 10;
    private static final int LONG_MA_PERIOD = 50;
    private static final int RSI_PERIOD = 14;
    private static final int ATR_PERIOD = 14;
    private static final int SHORT_EMA_PERIOD = 12;
    private static final int LONG_EMA_PERIOD = 26;
    private static final int SIGNAL_EMA_PERIOD = 9;
    private static final int ADX_PERIOD = 14;

    @Override
    public boolean shouldBuy(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        int currentIndex = historicalData.indexOf(currentData);
        if (currentIndex == -1) {
            return false; // currentData not found in historicalData
        }

        double shortSMA = calculateSMA(historicalData.subList(0, currentIndex + 1), SHORT_MA_PERIOD);
        double longSMA = calculateSMA(historicalData.subList(0, currentIndex + 1), LONG_MA_PERIOD);
        double rsi = calculateRSI(historicalData.subList(0, currentIndex + 1), RSI_PERIOD);
        double atr = calculateATR(historicalData.subList(0, currentIndex + 1), ATR_PERIOD);
        double macd = calculateMACD(historicalData.subList(0, currentIndex + 1));
        double adx = calculateADX(historicalData.subList(0, currentIndex + 1), ADX_PERIOD);

        return shortSMA > longSMA &&
                rsi < 50 &&
                macd > calculateMACDSignal(historicalData.subList(0, currentIndex + 1)) &&
                adx > 25 && // Adjust the threshold based on your preference
                atr > historicalData.get(Math.max(0, currentIndex - 1)).getVolumeTraded() &&
                currentData.getPriceClose() > currentData.getPriceOpen();
    }

    @Override
    public boolean shouldSell(CoinAPIResponse currentData, List<CoinAPIResponse> historicalData) {
        int currentIndex = historicalData.indexOf(currentData);
        if (currentIndex == -1) {
            return false; // currentData not found in historicalData
        }

        double shortSMA = calculateSMA(historicalData.subList(0, currentIndex + 1), SHORT_MA_PERIOD);
        double longSMA = calculateSMA(historicalData.subList(0, currentIndex + 1), LONG_MA_PERIOD);
        double rsi = calculateRSI(historicalData.subList(0, currentIndex + 1), RSI_PERIOD);
        double atr = calculateATR(historicalData.subList(0, currentIndex + 1), ATR_PERIOD);
        double macd = calculateMACD(historicalData.subList(0, currentIndex + 1));
        double adx = calculateADX(historicalData.subList(0, currentIndex + 1), ADX_PERIOD);

        return shortSMA < longSMA &&
                rsi > 50 &&
                macd < calculateMACDSignal(historicalData.subList(0, currentIndex + 1)) &&
                adx > 25 && // Adjust the threshold based on your preference
                atr > historicalData.get(Math.max(0, currentIndex - 1)).getVolumeTraded() &&
                currentData.getPriceClose() < currentData.getPriceOpen();
    }

    private double calculateSMA(List<CoinAPIResponse> data, int period) {
        double sum = 0;
        int endIndex = data.size() - 1;
        int startIndex = Math.max(0, endIndex - period + 1);

        for (int i = startIndex; i <= endIndex; i++) {
            sum += data.get(i).getPriceClose();
        }
        return sum / Math.max(1, period); // Avoid division by zero
    }

    private double calculateEMA(List<CoinAPIResponse> data, int period) {
        int endIndex = data.size() - 1;
        double multiplier = 2.0 / (period + 1);
        double ema = data.get(endIndex).getPriceClose();

        for (int i = endIndex - 1; i >= Math.max(0, endIndex - period); i--) {
            ema = (data.get(i).getPriceClose() - ema) * multiplier + ema;
        }

        return ema;
    }

    private double calculateRSI(List<CoinAPIResponse> data, int period) {
        int endIndex = data.size() - 1;
        int startIndex = Math.max(0, endIndex - period + 1);
        double avgGain = 0;
        double avgLoss = 0;

        for (int i = startIndex; i < endIndex; i++) {
            double priceDiff = data.get(i + 1).getPriceClose() - data.get(i).getPriceClose();
            if (priceDiff > 0) {
                avgGain += priceDiff;
            } else {
                avgLoss += Math.abs(priceDiff);
            }
        }

        avgGain /= Math.max(1, period); // Avoid division by zero
        avgLoss /= Math.max(1, period); // Avoid division by zero

        double rs = (avgLoss == 0) ? Double.POSITIVE_INFINITY : avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }


    private double calculateATR(List<CoinAPIResponse> data, int period) {
        int endIndex = data.size() - 1;
        int startIndex = Math.max(0, endIndex - period + 1);
        double trSum = 0;

        for (int i = startIndex + 1; i <= endIndex; i++) {
            double tr = Math.max(
                    data.get(i).getPriceHigh() - data.get(i).getPriceLow(),
                    Math.max(
                            Math.abs(data.get(i).getPriceHigh() - data.get(i - 1).getPriceClose()),
                            Math.abs(data.get(i).getPriceLow() - data.get(i - 1).getPriceClose())
                    )
            );
            trSum += tr;
        }

        return trSum / Math.max(1, period); // Avoid division by zero
    }


    private double calculateMACD(List<CoinAPIResponse> data) {
        return calculateEMA(data, SHORT_EMA_PERIOD) - calculateEMA(data, LONG_EMA_PERIOD);
    }

    private double calculateMACDSignal(List<CoinAPIResponse> data) {
        return calculateEMA(data, SIGNAL_EMA_PERIOD);
    }

    private double calculateADX(List<CoinAPIResponse> data, int period) {
        int endIndex = data.size() - 1;
        int startIndex = Math.max(0, endIndex - period + 1);
        double[] trArray = new double[period];
        double[] pdmArray = new double[period];
        double[] mdmArray = new double[period];

        for (int i = startIndex; i <= endIndex; i++) {
            double high = data.get(i).getPriceHigh();
            double low = data.get(i).getPriceLow();
            double prevHigh = (i > 0) ? data.get(i - 1).getPriceHigh() : high;
            double prevLow = (i > 0) ? data.get(i - 1).getPriceLow() : low;
            double prevClose = (i > 0) ? data.get(i - 1).getPriceClose() : data.get(i).getPriceOpen();

            double tr = Math.max(
                    high - low,
                    Math.max(
                            Math.abs(high - prevClose),
                            Math.abs(low - prevClose)
                    )
            );
            trArray[i % period] = tr;

            double moveUp = high - prevHigh;
            double moveDown = prevLow - low;

            pdmArray[i % period] = (moveUp > 0 && moveUp > moveDown) ? moveUp : 0;
            mdmArray[i % period] = (moveDown > 0 && moveDown > moveUp) ? moveDown : 0;
        }

        double avgTR = calculateAverage(trArray);
        double avgPDM = calculateAverage(pdmArray);
        double avgMDM = calculateAverage(mdmArray);

        double rs = (avgMDM == 0) ? Double.POSITIVE_INFINITY : avgPDM / avgMDM;
        double diPlus = (avgTR == 0) ? 0 : (avgPDM / avgTR) * 100;
        double diMinus = (avgTR == 0) ? 0 : (avgMDM / avgTR) * 100;

        double dx = (diPlus == 0 && diMinus == 0) ? 0 : (Math.abs(diPlus - diMinus) / Math.max(1, (diPlus + diMinus))) * 100;

        return calculateEMA(data.subList(0, endIndex + 1), dx, period);
    }

    private double calculateAverage(double[] array) {
        double sum = 0;
        for (double value : array) {
            sum += value;
        }
        return sum / Math.max(1, array.length); // Avoid division by zero
    }

    private double calculateEMA(List<CoinAPIResponse> data, double initialValue, int period) {
        int endIndex = data.size() - 1;
        double multiplier = 2.0 / (period + 1);
        double ema = initialValue;

        for (int i = endIndex; i >= 0; i--) {
            ema = (data.get(i).getPriceClose() - ema) * multiplier + ema;
        }

        return ema;
    }


}