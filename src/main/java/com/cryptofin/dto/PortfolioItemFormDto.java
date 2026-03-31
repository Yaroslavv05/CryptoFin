package com.cryptofin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PortfolioItemFormDto {
    @NotNull(message = "Coin is required")
    private Long coinId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00000001", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Buy price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal buyPriceUsd;

    @NotNull(message = "Buy date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate buyDate;
}
