package com.cryptofin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CoinDto {
    private Long id;
    private String coingeckoId;
    private String symbol;
    private String name;
    private String imageUrl;
    private BigDecimal priceUsd;
    private BigDecimal marketCap;
    private BigDecimal volume24h;
    private BigDecimal priceChangePercent24h;
}
