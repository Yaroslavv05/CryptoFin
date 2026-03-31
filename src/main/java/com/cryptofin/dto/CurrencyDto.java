package com.cryptofin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CurrencyDto {
    private Long id;
    private String code;
    private String name;
    private BigDecimal rateUah;
    private LocalDate rateDate;
}
