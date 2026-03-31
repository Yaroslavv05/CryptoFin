package com.cryptofin.controller;

import com.cryptofin.dto.CoinDto;
import com.cryptofin.dto.PriceHistoryDto;
import com.cryptofin.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CoinService coinService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        List<CoinDto> coins = (search != null && !search.isBlank())
                ? coinService.search(search)
                : coinService.getAllCoins();
        model.addAttribute("coins", coins);
        model.addAttribute("search", search);
        return "crypto/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CoinDto coin = coinService.getCoinById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coin not found: " + id));
        model.addAttribute("coin", coin);
        return "crypto/detail";
    }

    @GetMapping("/{id}/history")
    @ResponseBody
    public ResponseEntity<List<PriceHistoryDto>> priceHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "48") int points) {
        return ResponseEntity.ok(coinService.getPriceHistory(id, points));
    }
}
