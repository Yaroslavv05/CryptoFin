package com.cryptofin.parser;

import com.cryptofin.dto.CoinDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinGeckoClient {

    @Value("${app.coingecko.base-url}")
    private String baseUrl;

    @Value("${app.coins.top-count}")
    private int topCount;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<CoinDto> fetchTopCoins() {
        String url = baseUrl + "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=" + topCount
                + "&page=1&sparkline=false&price_change_percentage=24h";
        List<CoinDto> coins = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode array = objectMapper.readTree(response);
            for (JsonNode node : array) {
                CoinDto dto = CoinDto.builder()
                        .coingeckoId(node.path("id").asText())
                        .symbol(node.path("symbol").asText().toUpperCase())
                        .name(node.path("name").asText())
                        .imageUrl(node.path("image").asText())
                        .priceUsd(toBigDecimal(node, "current_price"))
                        .marketCap(toBigDecimal(node, "market_cap"))
                        .volume24h(toBigDecimal(node, "total_volume"))
                        .priceChangePercent24h(toBigDecimal(node, "price_change_percentage_24h"))
                        .build();
                coins.add(dto);
            }
            log.info("Fetched {} coins from CoinGecko", coins.size());
        } catch (Exception e) {
            log.error("Error fetching coins from CoinGecko: {}", e.getMessage());
        }
        return coins;
    }

    private BigDecimal toBigDecimal(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isNull() || value.isMissingNode()) return BigDecimal.ZERO;
        return new BigDecimal(value.asText());
    }
}
