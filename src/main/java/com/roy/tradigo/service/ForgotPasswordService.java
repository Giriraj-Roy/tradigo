package com.roy.tradigo.service;

import com.roy.tradigo.domain.VerificationType;
import com.roy.tradigo.model.ForgotPasswordToken;
import com.roy.tradigo.model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo);

    ForgotPasswordToken findById(String id);
    ForgotPasswordToken findByUser(Long userId);
    void deleteToken(ForgotPasswordToken token);

}
