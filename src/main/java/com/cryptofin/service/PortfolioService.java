package com.cryptofin.service;

import com.cryptofin.dto.PortfolioDto;
import com.cryptofin.dto.PortfolioFormDto;
import com.cryptofin.dto.PortfolioItemDto;
import com.cryptofin.dto.PortfolioItemFormDto;
import com.cryptofin.entity.Coin;
import com.cryptofin.entity.Portfolio;
import com.cryptofin.entity.PortfolioItem;
import com.cryptofin.repository.CoinPriceRepository;
import com.cryptofin.repository.CoinRepository;
import com.cryptofin.repository.PortfolioItemRepository;
import com.cryptofin.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final CoinRepository coinRepository;
    private final CoinPriceRepository coinPriceRepository;

    @Transactional(readOnly = true)
    public List<PortfolioDto> getAllPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortfolioDto getPortfolioById(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Portfolio not found: " + id));
        return toDto(portfolio);
    }

    @Transactional
    public PortfolioDto createPortfolio(PortfolioFormDto form) {
        Portfolio portfolio = Portfolio.builder()
                .name(form.getName())
                .description(form.getDescription())
                .build();
        return toDto(portfolioRepository.save(portfolio));
    }

    @Transactional
    public PortfolioDto updatePortfolio(Long id, PortfolioFormDto form) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Portfolio not found: " + id));
        portfolio.setName(form.getName());
        portfolio.setDescription(form.getDescription());
        return toDto(portfolioRepository.save(portfolio));
    }

    @Transactional
    public void deletePortfolio(Long id) {
        portfolioRepository.deleteById(id);
    }

    @Transactional
    public void addItem(Long portfolioId, PortfolioItemFormDto form) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new NoSuchElementException("Portfolio not found: " + portfolioId));
        Coin coin = coinRepository.findById(form.getCoinId())
                .orElseThrow(() -> new NoSuchElementException("Coin not found: " + form.getCoinId()));

        PortfolioItem item = PortfolioItem.builder()
                .portfolio(portfolio)
                .coin(coin)
                .amount(form.getAmount())
                .buyPriceUsd(form.getBuyPriceUsd())
                .buyDate(form.getBuyDate())
                .build();
        portfolioItemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        portfolioItemRepository.deleteById(itemId);
    }

    private PortfolioDto toDto(Portfolio portfolio) {
        List<PortfolioItemDto> items = portfolio.getItems().stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        BigDecimal totalInvested = items.stream()
                .map(PortfolioItemDto::getInvestedUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrent = items.stream()
                .map(PortfolioItemDto::getCurrentValueUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitLoss = totalCurrent.subtract(totalInvested);
        BigDecimal profitLossPercent = totalInvested.compareTo(BigDecimal.ZERO) != 0
                ? profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return PortfolioDto.builder()
                .id(portfolio.getId())
                .name(portfolio.getName())
                .description(portfolio.getDescription())
                .createdAt(portfolio.getCreatedAt())
                .items(items)
                .totalInvestedUsd(totalInvested)
                .totalCurrentUsd(totalCurrent)
                .profitLossUsd(profitLoss)
                .profitLossPercent(profitLossPercent)
                .build();
    }

    private PortfolioItemDto toItemDto(PortfolioItem item) {
        BigDecimal currentPrice = coinPriceRepository
                .findTopByCoinIdOrderByRecordedAtDesc(item.getCoin().getId())
                .map(cp -> cp.getPriceUsd())
                .orElse(item.getBuyPriceUsd());

        BigDecimal currentValue = currentPrice.multiply(item.getAmount());
        BigDecimal invested = item.getBuyPriceUsd().multiply(item.getAmount());
        BigDecimal profitLoss = currentValue.subtract(invested);
        BigDecimal profitLossPercent = invested.compareTo(BigDecimal.ZERO) != 0
                ? profitLoss.divide(invested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return PortfolioItemDto.builder()
                .id(item.getId())
                .portfolioId(item.getPortfolio().getId())
                .coinId(item.getCoin().getId())
                .coinName(item.getCoin().getName())
                .coinSymbol(item.getCoin().getSymbol())
                .coinImage(item.getCoin().getImageUrl())
                .amount(item.getAmount())
                .buyPriceUsd(item.getBuyPriceUsd())
                .buyDate(item.getBuyDate())
                .currentPriceUsd(currentPrice)
                .currentValueUsd(currentValue)
                .investedUsd(invested)
                .profitLossUsd(profitLoss)
                .profitLossPercent(profitLossPercent)
                .build();
    }
}
