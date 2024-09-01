package com.roy.tradigo.service;

import com.roy.tradigo.config.JwtProvider;
import com.roy.tradigo.domain.VerificationType;
import com.roy.tradigo.model.TwoFactorAuth;
import com.roy.tradigo.model.User;
import com.roy.tradigo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User findUserByJwt(String jwt) throws Exception {
        String email= JwtProvider.getEmailFromJwtToken(jwt);
        User user = userRepository.findByEmail(email);

        if(user==null){
            throw new Exception("User Not Found");
        }

        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
//        String email= JwtProvider.getEmailFromJwtToken(jwt);
        User user = userRepository.findByEmail(email);

        if(user==null){
            throw new Exception("User Not Found");
        }

        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user=userRepository.findById(userId);
        if(user.isEmpty()){
            throw new Exception("User Not Found");
        }
        return user.get();
    }

    @Override
    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth=new TwoFactorAuth();
        twoFactorAuth.set_enabled(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);


        return userRepository.save(user);
    }


    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
