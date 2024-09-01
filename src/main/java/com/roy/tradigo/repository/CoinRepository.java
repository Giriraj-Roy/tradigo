package com.roy.tradigo.repository;

import com.roy.tradigo.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin,String> {
}
