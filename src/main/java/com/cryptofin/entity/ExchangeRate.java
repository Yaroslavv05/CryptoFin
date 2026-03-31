package com.cryptofin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "exchange_rates", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"currency_id", "rate_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(name = "rate_uah", precision = 20, scale = 6, nullable = false)
    private BigDecimal rateUah;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;
}
