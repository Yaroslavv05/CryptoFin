package com.cryptofin.service;

import com.cryptofin.dto.CoinDto;
import com.cryptofin.dto.PriceHistoryDto;
import com.cryptofin.entity.Coin;
import com.cryptofin.entity.CoinPrice;
import com.cryptofin.parser.CoinGeckoClient;
import com.cryptofin.repository.CoinPriceRepository;
import com.cryptofin.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {

    private final CoinRepository coinRepository;
    private final CoinPriceRepository coinPriceRepository;
    private final CoinGeckoClient coinGeckoClient;

    @Transactional(readOnly = true)
    public List<CoinDto> getAllCoins() {
        return coinRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CoinDto> getCoinById(Long id) {
        return coinRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<CoinDto> search(String query) {
        return coinRepository
                .findByNameContainingIgnoreCaseOrSymbolContainingIgnoreCase(query, query)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PriceHistoryDto> getPriceHistory(Long coinId, int limit) {
        return coinPriceRepository.findTopNByCoinId(coinId, limit).stream()
                .map(cp -> new PriceHistoryDto(cp.getRecordedAt(), cp.getPriceUsd()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void refreshPrices() {
        List<CoinDto> fetched = coinGeckoClient.fetchTopCoins();
        for (CoinDto dto : fetched) {
            Coin coin = coinRepository.findByCoingeckoId(dto.getCoingeckoId())
                    .orElseGet(() -> coinRepository.save(Coin.builder()
                            .coingeckoId(dto.getCoingeckoId())
                            .symbol(dto.getSymbol())
                            .name(dto.getName())
                            .imageUrl(dto.getImageUrl())
                            .build()));

            coin.setImageUrl(dto.getImageUrl());

            CoinPrice price = CoinPrice.builder()
                    .coin(coin)
                    .priceUsd(dto.getPriceUsd())
                    .marketCap(dto.getMarketCap())
                    .volume24h(dto.getVolume24h())
                    .priceChangePercent24h(dto.getPriceChangePercent24h())
                    .build();
            coinPriceRepository.save(price);
        }
        log.info("Refreshed prices for {} coins", fetched.size());
    }

    public CoinDto toDto(Coin coin) {
        CoinPrice latest = coinPriceRepository
                .findTopByCoinIdOrderByRecordedAtDesc(coin.getId())
                .orElse(null);
        return CoinDto.builder()
                .id(coin.getId())
                .coingeckoId(coin.getCoingeckoId())
                .symbol(coin.getSymbol())
                .name(coin.getName())
                .imageUrl(coin.getImageUrl())
                .priceUsd(latest != null ? latest.getPriceUsd() : null)
                .marketCap(latest != null ? latest.getMarketCap() : null)
                .volume24h(latest != null ? latest.getVolume24h() : null)
                .priceChangePercent24h(latest != null ? latest.getPriceChangePercent24h() : null)
                .build();
    }
}
