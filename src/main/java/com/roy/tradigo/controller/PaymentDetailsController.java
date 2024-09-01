package com.roy.tradigo.controller;

import com.roy.tradigo.model.PaymentDetails;
import com.roy.tradigo.model.User;
import com.roy.tradigo.service.PaymentDetailsService;
import com.roy.tradigo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentDetailsController {

    @Autowired
    private UserService userService;
    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @PostMapping("/payment-details")
    public ResponseEntity<PaymentDetails> addPaymentDetails(
            @RequestHeader("Authorization") String jwt,
            @RequestBody PaymentDetails paymentDetailsReq
    ) throws Exception {
        User user=userService.findUserByJwt(jwt);

        PaymentDetails paymentDetails=paymentDetailsService.addPaymentDetails(
                paymentDetailsReq.getAccountNumber(),
                paymentDetailsReq.getAccountHolderName(),
                paymentDetailsReq.getIfsc(),
                paymentDetailsReq.getBankName(),
                user
        );
        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping("/payment-details")
    public ResponseEntity<PaymentDetails> addPaymentDetails(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user=userService.findUserByJwt(jwt);

        PaymentDetails paymentDetails=paymentDetailsService.getUserPaymentDetails(user);
        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

}
