package com.roy.tradigo.service;

import com.roy.tradigo.model.Coin;
import com.roy.tradigo.model.User;
import com.roy.tradigo.model.Watchlist;

public interface WatchlistService {
    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin, User user) throws Exception;



}
