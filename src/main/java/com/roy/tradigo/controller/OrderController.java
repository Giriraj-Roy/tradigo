package com.roy.tradigo.controller;

import com.roy.tradigo.domain.OrderType;
import com.roy.tradigo.model.Coin;
import com.roy.tradigo.model.Order;
import com.roy.tradigo.model.User;
import com.roy.tradigo.request.CreateOrderRequest;
import com.roy.tradigo.service.CoinService;
import com.roy.tradigo.service.OrderService;
import com.roy.tradigo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private CoinService coinService;

//    @Autowired
//    private WalletTransactionService walletTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest req
    ) throws Exception {
        User user=userService.findUserByJwt(jwt);
        Coin coin=coinService.findById(req.getCoinId());

        Order order=orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId
    ) throws Exception {
        User user=userService.findUserByJwt(jwt);
        Order order=orderService.getOrderById(orderId);

        if(order.getUser().getId().equals(user.getId())){
            return ResponseEntity.ok(order);
        }
        else{
            throw new Exception("Access Not Permitted");
        }

    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrderForUser(
        @RequestHeader("Authorization") String jwt,
        @RequestParam(required = false) OrderType orderType,
        @RequestParam(required = false) String assetSymbol
    ) throws Exception {
        if(jwt==null)   throw new Exception("Token Missing");

        Long userId = userService.findUserByJwt(jwt).getId();
        List<Order> userOrders=orderService.getAllOrdersOfUser(userId, orderType, assetSymbol);

        return ResponseEntity.ok(userOrders);
    }

}
