package com.cryptofin.parser;

import com.cryptofin.dto.CurrencyDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NbuClient {

    private static final String API_URL = "https://open.er-api.com/v6/latest/UAH";

    private static final Map<String, String> CURRENCY_NAMES = Map.of(
            "USD", "US Dollar",
            "EUR", "Euro",
            "PLN", "Polish Zloty",
            "GBP", "British Pound",
            "CHF", "Swiss Franc",
            "CZK", "Czech Koruna",
            "JPY", "Japanese Yen",
            "CNY", "Chinese Yuan"
    );

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<CurrencyDto> fetchTodayRates() {
        List<CurrencyDto> rates = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(API_URL, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode ratesNode = root.path("rates");

            for (Map.Entry<String, String> entry : CURRENCY_NAMES.entrySet()) {
                String code = entry.getKey();
                JsonNode rateNode = ratesNode.path(code);
                if (rateNode.isMissingNode() || rateNode.isNull()) continue;

                // API returns: 1 UAH = X currency, so 1 currency = 1/X UAH
                double uahPerCurrency = 1.0 / rateNode.asDouble();
                CurrencyDto dto = CurrencyDto.builder()
                        .code(code)
                        .name(entry.getValue())
                        .rateUah(BigDecimal.valueOf(uahPerCurrency).setScale(4, RoundingMode.HALF_UP))
                        .rateDate(LocalDate.now())
                        .build();
                rates.add(dto);
            }
            log.info("Fetched {} currency rates from ExchangeRate API", rates.size());
        } catch (Exception e) {
            log.error("Error fetching currency rates: {}", e.getMessage());
        }
        return rates;
    }
}
