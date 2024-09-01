package com.roy.tradigo.service;

import com.roy.tradigo.domain.VerificationType;
import com.roy.tradigo.model.User;


public interface UserService {

    public User findUserByJwt(String jwt) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long userId) throws Exception;
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user);

    User updatePassword(User user, String newPassword);


}
