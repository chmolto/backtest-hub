package com.example.backtesthub.service;

import com.example.backtesthub.models.CoinAPIRequest;
import com.example.backtesthub.models.CoinAPIResponse;
import com.example.backtesthub.strategies.TradingStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CoinAPIService {

    @Value("${coinapi.apiKey}")
    private String apiKey;

    private static final String BASE_URL = "https://rest.coinapi.io/v1/";

    private final WebClient.Builder webClientBuilder;

    private static final String DATA_FILE_PATH = "historical_data.json";

    private final ObjectMapper objectMapper;

    public CoinAPIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    public List<CoinAPIResponse> getHistoricalData(CoinAPIRequest request) {
        List<CoinAPIResponse> historicalData;

        // Try to load historical data from the file
        try {
            historicalData = loadHistoricalDataFromFile();
        } catch (IOException e) {
            // If the file doesn't exist or there's an issue reading it, make a request to the API
            historicalData = getHistoricalDataFromAPI(request);

            // Save the fetched data to the file
            saveHistoricalDataToFile(historicalData);
        }

        return historicalData;
    }

    private List<CoinAPIResponse> loadHistoricalDataFromFile() throws IOException {
        return List.of(objectMapper.readValue(new File(DATA_FILE_PATH), CoinAPIResponse[].class));
    }

    private void saveHistoricalDataToFile(List<CoinAPIResponse> historicalData) {
        try {
            objectMapper.writeValue(new File(DATA_FILE_PATH), historicalData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<CoinAPIResponse> getHistoricalDataFromAPI(CoinAPIRequest request) {
        String url = BASE_URL + buildHistoricalDataUrl(request);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .header("X-CoinAPI-Key", apiKey)
                .retrieve()
                .bodyToFlux(CoinAPIResponse.class)
                .collectList()
                .block();
    }

    private String buildHistoricalDataUrl(CoinAPIRequest request) {
        StringBuilder urlBuilder = new StringBuilder("ohlcv/")
                .append(request.getSymbolId())
                .append("/history")
                .append("?period_id=").append(request.getPeriodId())
                .append("&time_start=").append(request.getTimeStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (request.getTimeEnd() != null) {
            urlBuilder.append("&time_end=").append(request.getTimeEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        urlBuilder.append("&limit=").append(request.getLimit())
                .append("&include_empty_items=").append(request.isIncludeEmptyItems());

        return urlBuilder.toString();
    }

}