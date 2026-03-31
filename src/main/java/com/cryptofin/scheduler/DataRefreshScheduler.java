package com.cryptofin.scheduler;

import com.cryptofin.service.CoinService;
import com.cryptofin.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataRefreshScheduler {

    private final CoinService coinService;
    private final CurrencyService currencyService;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Application started — loading initial data...");
        refreshAll();
    }

    @Scheduled(fixedRateString = "1800000") // every 30 minutes
    public void scheduledRefresh() {
        log.info("Scheduled data refresh started");
        refreshAll();
    }

    private void refreshAll() {
        try {
            coinService.refreshPrices();
        } catch (Exception e) {
            log.error("Failed to refresh coin prices: {}", e.getMessage());
        }
        try {
            currencyService.refreshRates();
        } catch (Exception e) {
            log.error("Failed to refresh currency rates: {}", e.getMessage());
        }
    }
}
