package com.example.backtesthub.charts;

import com.example.backtesthub.models.CoinAPIResponse;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class ChartsImageSaver<Strategy> {

    protected final int width;
    protected final int height;

    public ChartsImageSaver(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void generateAndSaveChart(List<CoinAPIResponse> data, Strategy strategy, String filePath) {
        TimeSeriesCollection dataset = createTimeSeriesDataset(data, strategy);
        JFreeChart chart = createChart(dataset, strategy);
        File imageFile = new File("charts\\" + filePath);
        try {
            ChartUtils.saveChartAsPNG(imageFile, chart, width, height);
        } catch (IOException e) {
            System.err.println("Error occurred while saving the chart: " + e.getMessage());
        }
    }

    protected abstract JFreeChart createChart(TimeSeriesCollection dataset, Strategy strategy);

    protected abstract TimeSeriesCollection createTimeSeriesDataset(List<CoinAPIResponse> data, Strategy strategy);
}
