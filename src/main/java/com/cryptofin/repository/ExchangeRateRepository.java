package com.cryptofin.repository;

import com.cryptofin.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCurrencyIdAndRateDate(Long currencyId, LocalDate rateDate);

    Optional<ExchangeRate> findTopByCurrencyIdOrderByRateDateDesc(Long currencyId);

    boolean existsByCurrencyCodeAndRateDate(String code, LocalDate rateDate);

    @Query("SELECT er FROM ExchangeRate er WHERE er.currency.id = :currencyId ORDER BY er.rateDate DESC LIMIT :limit")
    List<ExchangeRate> findTopNByCurrencyId(@Param("currencyId") Long currencyId, @Param("limit") int limit);
}
