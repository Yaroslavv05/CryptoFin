package com.cryptofin.controller;

import com.cryptofin.dto.CoinDto;
import com.cryptofin.dto.CurrencyDto;
import com.cryptofin.service.CoinService;
import com.cryptofin.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CoinService coinService;
    private final CurrencyService currencyService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<CoinDto> coins = coinService.getAllCoins();
        List<CurrencyDto> currencies = currencyService.getAllWithLatestRate();

        model.addAttribute("topCoins", coins.stream().limit(10).toList());
        model.addAttribute("currencies", currencies);
        model.addAttribute("totalCoins", coins.size());
        return "dashboard";
    }
}
