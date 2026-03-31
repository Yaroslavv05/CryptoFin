package com.cryptofin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coin_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;

    @Column(precision = 24, scale = 8)
    private BigDecimal priceUsd;

    @Column(precision = 30, scale = 2)
    private BigDecimal marketCap;

    @Column(precision = 30, scale = 2)
    private BigDecimal volume24h;

    @Column(precision = 10, scale = 4)
    private BigDecimal priceChangePercent24h;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        if (recordedAt == null) recordedAt = LocalDateTime.now();
    }
}
