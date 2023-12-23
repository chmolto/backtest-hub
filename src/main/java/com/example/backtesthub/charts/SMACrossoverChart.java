package com.example.backtesthub.charts;

import com.example.backtesthub.models.CoinAPIResponse;
import com.example.backtesthub.strategies.SMACrossoverStrategy;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SMACrossoverChart extends ChartsImageSaver<SMACrossoverStrategy> {

    public SMACrossoverChart() {
        super(5120, 2880);
    }

    @Override
    protected JFreeChart createChart(TimeSeriesCollection dataset, SMACrossoverStrategy strategy) {
        return ChartFactory.createTimeSeriesChart(
                "SMACrossover Strategy Chart", // Chart title
                "Time", // x-axis label
                "Price", // y-axis label
                dataset, // Dataset
                true, // Include legend
                true, // Tooltips
                false // URLs
        );
        // Additional chart customization (colors, markers, etc.) can be added here
    }

    @Override
    protected TimeSeriesCollection createTimeSeriesDataset(List<CoinAPIResponse> data, SMACrossoverStrategy strategy) {
        TimeSeries priceSeries = new TimeSeries("Price");
        TimeSeries shortTermMASeries = new TimeSeries("Short-Term MA");
        TimeSeries longTermMASeries = new TimeSeries("Long-Term MA");

        MovingAverage shortTermMA = new MovingAverage(strategy.getShortTermPeriod());
        MovingAverage longTermMA = new MovingAverage(strategy.getLongTermPeriod());

        for (CoinAPIResponse response : data) {
            LocalDate localDate = response.getTimePeriodEnd().toLocalDate();
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Day currentDay = new Day(date);

            // Add price data
            priceSeries.addOrUpdate(currentDay, response.getPriceClose());

            // Update and add moving averages
            shortTermMA.add(response.getPriceClose());
            longTermMA.add(response.getPriceClose());

            if (shortTermMA.isReady()) {
                shortTermMASeries.addOrUpdate(currentDay, shortTermMA.getAverage());
            }
            if (longTermMA.isReady()) {
                longTermMASeries.addOrUpdate(currentDay, longTermMA.getAverage());
            }
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(priceSeries);
        dataset.addSeries(shortTermMASeries);
        dataset.addSeries(longTermMASeries);

        return dataset;
    }

    // Helper class to calculate moving averages
    class MovingAverage {
        private final Queue<Double> window = new LinkedList<>();
        private final int period;
        private double sum = 0.0;

        public MovingAverage(int period) {
            this.period = period;
        }

        public void add(double num) {
            sum += num;
            window.add(num);
            if (window.size() > period) {
                sum -= window.remove();
            }
        }

        public double getAverage() {
            return window.size() == period ? sum / period : Double.NaN;
        }

        public boolean isReady() {
            return window.size() == period;
        }
    }

}