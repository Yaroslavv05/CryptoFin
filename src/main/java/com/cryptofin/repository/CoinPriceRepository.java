package com.cryptofin.repository;

import com.cryptofin.entity.CoinPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoinPriceRepository extends JpaRepository<CoinPrice, Long> {

    Optional<CoinPrice> findTopByCoinIdOrderByRecordedAtDesc(Long coinId);

    @Query("SELECT cp FROM CoinPrice cp WHERE cp.coin.id = :coinId ORDER BY cp.recordedAt DESC")
    List<CoinPrice> findLatestByCoinId(@Param("coinId") Long coinId);

    @Query("SELECT cp FROM CoinPrice cp WHERE cp.coin.id = :coinId ORDER BY cp.recordedAt DESC LIMIT :limit")
    List<CoinPrice> findTopNByCoinId(@Param("coinId") Long coinId, @Param("limit") int limit);
}
