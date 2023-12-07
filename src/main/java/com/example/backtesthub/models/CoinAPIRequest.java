package com.example.backtesthub.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CoinAPIRequest{
    private String symbolId;
    private String periodId;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private int limit;
    private boolean includeEmptyItems;
}
