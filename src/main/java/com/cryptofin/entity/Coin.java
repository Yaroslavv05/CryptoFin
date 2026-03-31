package com.cryptofin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String coingeckoId;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;

    private String imageUrl;

    @OneToMany(mappedBy = "coin", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("recordedAt DESC")
    @Builder.Default
    private List<CoinPrice> prices = new ArrayList<>();

    @OneToMany(mappedBy = "coin", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PortfolioItem> portfolioItems = new ArrayList<>();
}
