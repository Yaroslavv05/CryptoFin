package com.cryptofin.controller;

import com.cryptofin.dto.PortfolioFormDto;
import com.cryptofin.dto.PortfolioItemFormDto;
import com.cryptofin.service.CoinService;
import com.cryptofin.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final CoinService coinService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("portfolios", portfolioService.getAllPortfolios());
        model.addAttribute("form", new PortfolioFormDto());
        return "portfolio/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") PortfolioFormDto form,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("portfolios", portfolioService.getAllPortfolios());
            return "portfolio/list";
        }
        portfolioService.createPortfolio(form);
        return "redirect:/portfolio";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("portfolio", portfolioService.getPortfolioById(id));
        model.addAttribute("coins", coinService.getAllCoins());
        model.addAttribute("itemForm", new PortfolioItemFormDto());
        return "portfolio/detail";
    }

    @PostMapping("/{id}/items")
    public String addItem(@PathVariable Long id,
                          @Valid @ModelAttribute("itemForm") PortfolioItemFormDto form,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("portfolio", portfolioService.getPortfolioById(id));
            model.addAttribute("coins", coinService.getAllCoins());
            model.addAttribute("showAddModal", true);
            return "portfolio/detail";
        }
        portfolioService.addItem(id, form);
        return "redirect:/portfolio/" + id;
    }

    @PostMapping("/{id}/items/{itemId}/delete")
    public String deleteItem(@PathVariable Long id, @PathVariable Long itemId) {
        portfolioService.deleteItem(itemId);
        return "redirect:/portfolio/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return "redirect:/portfolio";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var portfolio = portfolioService.getPortfolioById(id);
        PortfolioFormDto form = new PortfolioFormDto();
        form.setName(portfolio.getName());
        form.setDescription(portfolio.getDescription());
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("form", form);
        return "portfolio/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("form") PortfolioFormDto form,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("portfolio", portfolioService.getPortfolioById(id));
            return "portfolio/edit";
        }
        portfolioService.updatePortfolio(id, form);
        return "redirect:/portfolio/" + id;
    }
}
