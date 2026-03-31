package com.cryptofin.service;

import com.cryptofin.dto.CurrencyDto;
import com.cryptofin.entity.Currency;
import com.cryptofin.entity.ExchangeRate;
import com.cryptofin.parser.NbuClient;
import com.cryptofin.repository.CurrencyRepository;
import com.cryptofin.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final NbuClient nbuClient;

    @Transactional(readOnly = true)
    public List<CurrencyDto> getAllWithLatestRate() {
        return currencyRepository.findAll().stream()
                .map(this::toDtoWithLatest)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CurrencyDto> getRateHistory(Long currencyId, int days) {
        return exchangeRateRepository.findTopNByCurrencyId(currencyId, days).stream()
                .map(er -> CurrencyDto.builder()
                        .id(er.getCurrency().getId())
                        .code(er.getCurrency().getCode())
                        .name(er.getCurrency().getName())
                        .rateUah(er.getRateUah())
                        .rateDate(er.getRateDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void refreshRates() {
        List<CurrencyDto> fetched = nbuClient.fetchTodayRates();
        LocalDate today = LocalDate.now();
        for (CurrencyDto dto : fetched) {
            if (exchangeRateRepository.existsByCurrencyCodeAndRateDate(dto.getCode(), today)) {
                continue;
            }
            Currency currency = currencyRepository.findByCode(dto.getCode())
                    .orElseGet(() -> currencyRepository.save(Currency.builder()
                            .code(dto.getCode())
                            .name(dto.getName())
                            .build()));

            ExchangeRate rate = ExchangeRate.builder()
                    .currency(currency)
                    .rateUah(dto.getRateUah())
                    .rateDate(today)
                    .build();
            exchangeRateRepository.save(rate);
        }
        log.info("Refreshed exchange rates for {} currencies", fetched.size());
    }

    private CurrencyDto toDtoWithLatest(Currency currency) {
        ExchangeRate latest = exchangeRateRepository
                .findTopByCurrencyIdOrderByRateDateDesc(currency.getId())
                .orElse(null);
        return CurrencyDto.builder()
                .id(currency.getId())
                .code(currency.getCode())
                .name(currency.getName())
                .rateUah(latest != null ? latest.getRateUah() : null)
                .rateDate(latest != null ? latest.getRateDate() : null)
                .build();
    }
}
