package com.roy.tradigo.service;

import com.razorpay.RazorpayException;
import com.roy.tradigo.domain.PaymentMethod;
import com.roy.tradigo.model.PaymentOrder;
import com.roy.tradigo.model.User;
import com.roy.tradigo.response.PaymentResponse;

public interface PaymentService {
    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);
    PaymentOrder getPaymentOrderById(Long id) throws Exception;
    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException;
    PaymentResponse createRazorpayPaymentLink(User user, Long amount) throws RazorpayException;
    PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId);
}
