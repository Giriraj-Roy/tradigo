package com.roy.tradigo.service;

import com.roy.tradigo.domain.VerificationType;
import com.roy.tradigo.model.User;
import com.roy.tradigo.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);

    void deleteVerificationCodeById(VerificationCode verificationCode);
}
