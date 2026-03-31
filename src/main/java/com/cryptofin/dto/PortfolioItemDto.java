package com.cryptofin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PortfolioItemDto {
    private Long id;
    private Long portfolioId;
    private Long coinId;
    private String coinName;
    private String coinSymbol;
    private String coinImage;
    private BigDecimal amount;
    private BigDecimal buyPriceUsd;
    private LocalDate buyDate;
    private BigDecimal currentPriceUsd;
    private BigDecimal currentValueUsd;
    private BigDecimal investedUsd;
    private BigDecimal profitLossUsd;
    private BigDecimal profitLossPercent;
}
