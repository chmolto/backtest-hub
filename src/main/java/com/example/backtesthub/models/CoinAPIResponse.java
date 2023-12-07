package com.example.backtesthub.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CoinAPIResponse{
    private LocalDateTime timePeriodStart;
    private LocalDateTime timePeriodEnd;
    private LocalDateTime timeOpen;
    private LocalDateTime timeClose;
    private Double priceOpen;
    private Double priceHigh;
    private Double priceLow;
    private Double priceClose;
    private Double volumeTraded;
    private Long tradesCount;
}
