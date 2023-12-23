package com.example.backtesthub.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CoinAPIResponse {

    @JsonAlias("time_period_start")
    private LocalDateTime timePeriodStart;

    @JsonAlias("time_period_end")
    private LocalDateTime timePeriodEnd;

    @JsonAlias("time_open")
    private LocalDateTime timeOpen;

    @JsonAlias("time_close")
    private LocalDateTime timeClose;

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

}
