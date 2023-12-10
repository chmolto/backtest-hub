package com.example.backtesthub.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class CoinAPIResponse {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");

    @JsonAlias("time_period_start")
    private String timePeriodStart;

    @JsonAlias("time_period_end")
    private String timePeriodEnd;

    @JsonAlias("time_open")
    private String timeOpen;

    @JsonAlias("time_close")
    private String timeClose;

    @JsonAlias("price_open")
    private Double priceOpen;

    @JsonAlias("price_high")
    private Double priceHigh;

    @JsonAlias("price_low")
    private Double priceLow;

    @JsonAlias("price_close")
    private Double priceClose;

    @JsonAlias("volume_traded")
    private Double volumeTraded;

    @JsonAlias("trades_count")
    private Long tradesCount;

    public LocalDateTime getTimePeriodStart() {
        return LocalDateTime.parse(timePeriodStart, DATE_TIME_FORMATTER);
    }

    public LocalDateTime getTimePeriodEnd() {
        return LocalDateTime.parse(timePeriodEnd, DATE_TIME_FORMATTER);
    }

    public LocalDateTime getTimeOpen() {
        return LocalDateTime.parse(timeOpen, DATE_TIME_FORMATTER);
    }

    public LocalDateTime getTimeClose() {
        return LocalDateTime.parse(timeClose, DATE_TIME_FORMATTER);
    }

}
