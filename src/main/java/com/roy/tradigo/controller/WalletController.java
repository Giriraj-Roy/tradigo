package com.roy.tradigo.controller;

import com.roy.tradigo.model.*;
import com.roy.tradigo.response.PaymentResponse;
import com.roy.tradigo.service.OrderService;
import com.roy.tradigo.service.PaymentService;
import com.roy.tradigo.service.UserService;
import com.roy.tradigo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/api/wallet/")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception {
        User user =userService.findUserByJwt(jwt);

        Wallet wallet=walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);

    }
    @PutMapping("/api/wallet/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction req
            ) throws Exception {

            User senderUser=userService.findUserByJwt(jwt);
            Wallet receiverWallet = walletService.findWalletById(walletId);
            Wallet wallet=walletService.walletToWalletTransfer(senderUser, receiverWallet, req.getAmount());

            return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }
    @PutMapping("/api/wallet/order/{orderId}/pay")
    public ResponseEntity<Wallet> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId
    ) throws Exception {

        User senderUser=userService.findUserByJwt(jwt);
        Order order=orderService.getOrderById(orderId);

        Wallet wallet=walletService.payOrderPayment(order, senderUser);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/deposit")
    public ResponseEntity<Wallet> addBalanceToWallet(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(name = "order_id") Long orderId,
            @RequestParam(name = "payment_id") String paymentId
    ) throws Exception {

        User user=userService.findUserByJwt(jwt);
        Wallet wallet=walletService.getUserWallet(user);
        PaymentOrder order=paymentService.getPaymentOrderById(orderId);

        Boolean status=paymentService.proceedPaymentOrder(order, paymentId);

        if(status){
            wallet=walletService.addBalance(wallet, order.getAmount());
        }


        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

}
