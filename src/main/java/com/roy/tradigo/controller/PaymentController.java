package com.roy.tradigo.controller;

import com.roy.tradigo.domain.PaymentMethod;
import com.roy.tradigo.model.PaymentOrder;
import com.roy.tradigo.model.User;
import com.roy.tradigo.response.PaymentResponse;
import com.roy.tradigo.service.PaymentService;
import com.roy.tradigo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt
            ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        PaymentResponse paymentResponse = new PaymentResponse();
        PaymentOrder order=paymentService.createOrder(user, amount, paymentMethod);

        if(paymentMethod.equals(PaymentMethod.RAZORPAY)){
            paymentResponse=paymentService.createRazorpayPaymentLink(user, amount);
        }

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

}
