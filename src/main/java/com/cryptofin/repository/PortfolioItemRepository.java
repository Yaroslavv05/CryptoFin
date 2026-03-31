package com.cryptofin.repository;

import com.cryptofin.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findByPortfolioId(Long portfolioId);
    void deleteByPortfolioId(Long portfolioId);
}
