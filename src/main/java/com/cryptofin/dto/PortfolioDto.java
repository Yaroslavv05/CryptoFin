package com.cryptofin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PortfolioDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private List<PortfolioItemDto> items;
    private BigDecimal totalInvestedUsd;
    private BigDecimal totalCurrentUsd;
    private BigDecimal profitLossUsd;
    private BigDecimal profitLossPercent;
}
