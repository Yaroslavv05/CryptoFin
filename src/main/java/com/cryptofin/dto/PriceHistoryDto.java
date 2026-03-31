package com.cryptofin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PriceHistoryDto {
    private LocalDateTime timestamp;
    private BigDecimal priceUsd;
}
