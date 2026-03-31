package com.cryptofin.controller;

import com.cryptofin.dto.CurrencyDto;
import com.cryptofin.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currencies", currencyService.getAllWithLatestRate());
        return "currency/list";
    }

    @GetMapping("/{id}/history")
    @ResponseBody
    public ResponseEntity<List<CurrencyDto>> history(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(currencyService.getRateHistory(id, days));
    }
}
