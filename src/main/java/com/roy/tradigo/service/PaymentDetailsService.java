package com.roy.tradigo.service;

import com.roy.tradigo.model.PaymentDetails;
import com.roy.tradigo.model.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails(String accountNumber,
                                            String accountHolderName,
                                            String ifsc,
                                            String bankName,
                                            User user);

    public PaymentDetails getUserPaymentDetails(User user);


}
