package com.roy.tradigo.controller;

import com.roy.tradigo.model.Coin;
import com.roy.tradigo.model.User;
import com.roy.tradigo.model.Watchlist;
import com.roy.tradigo.service.CoinService;
import com.roy.tradigo.service.UserService;
import com.roy.tradigo.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    @Autowired
    private WatchlistService watchlistService;
    @Autowired
    private UserService userService;
    @Autowired
    private CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<Watchlist> getUserWatchlist(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwt(jwt);
        Watchlist watchlist=watchlistService.findUserWatchlist(user.getId());

        return ResponseEntity.ok().body(watchlist);
    }

//    @PostMapping("/create")
//    public ResponseEntity<Watchlist> createWatchlist(
//            @RequestHeader("Authorization") String jwt
//    ) throws Exception {
//        User user=userService.findUserByJwt(jwt);
//        Watchlist watchlist=watchlistService.createWatchlist(user);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(watchlist);
//    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<Watchlist> getWatchlistById(
            @PathVariable Long watchlistId
    ) throws Exception {

        Watchlist watchlist=watchlistService.findById(watchlistId);

        return ResponseEntity.ok().body(watchlist);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId
    ) throws Exception {
        User user=userService.findUserByJwt(jwt);
        Coin coin=coinService.findById(coinId);
        Coin addedCoin =watchlistService.addItemToWatchlist(coin, user);

        return ResponseEntity.ok(addedCoin);
    }
}
