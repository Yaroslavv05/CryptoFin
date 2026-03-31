package com.cryptofin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "portfolio_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;

    @Column(precision = 24, scale = 8, nullable = false)
    private BigDecimal amount;

    @Column(precision = 24, scale = 8, nullable = false)
    private BigDecimal buyPriceUsd;

    @Column(nullable = false)
    private LocalDate buyDate;
}
