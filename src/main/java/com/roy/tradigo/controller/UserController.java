package com.roy.tradigo.controller;

import com.roy.tradigo.request.ForgotPasswordTokenRequest;
import com.roy.tradigo.domain.VerificationType;
import com.roy.tradigo.model.ForgotPasswordToken;
import com.roy.tradigo.model.User;
import com.roy.tradigo.model.VerificationCode;
import com.roy.tradigo.request.ResetPasswordRequest;
import com.roy.tradigo.response.APIResponse;
import com.roy.tradigo.response.AuthResponse;
import com.roy.tradigo.service.EmailService;
import com.roy.tradigo.service.ForgotPasswordService;
import com.roy.tradigo.service.UserService;
import com.roy.tradigo.service.VerificationCodeService;
import com.roy.tradigo.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private ForgotPasswordService forgotPasswordService;
    private String jwt;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user= userService.findUserByJwt(jwt);

        return new ResponseEntity<User>(user, HttpStatus.OK);

    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationType) throws Exception {

        User user= userService.findUserByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if(verificationCode==null){
//            verificationCodeService.deleteVerificationCodeById(verificationCode);
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }
        if(verificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }



        return new ResponseEntity<String>("Verification OTP sent Successfully", HttpStatus.OK);

    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
            @PathVariable String otp,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user= userService.findUserByJwt(jwt);

        VerificationCode verificationCode=verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)
                        ? verificationCode.getEmail()
                        : verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if(isVerified){
            User updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), sendTo, user);
            verificationCodeService.deleteVerificationCodeById(verificationCode);

            return new ResponseEntity<User>(updatedUser, HttpStatus.OK);

        }

        throw new Exception("Wrong OTP");

    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest req) throws Exception {

        User user=userService.findUserByEmail(req.getSendTo());
        String otp= OtpUtils.generateOtp();

        UUID uuid=UUID.randomUUID();
        String id=uuid.toString();

        ForgotPasswordToken token= forgotPasswordService.findByUser(user.getId());

        if(token==null){
            token= forgotPasswordService.createToken(user, id, otp, req.getVerificationType(), req.getSendTo());
        }

        if(req.getVerificationType().equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
        }

        AuthResponse response=new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password Reset OTP sent successfully !");



        return new ResponseEntity<AuthResponse>(response, HttpStatus.OK);

    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<APIResponse> resetPassword(
            @RequestParam String id,
            @RequestBody ResetPasswordRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {

        ForgotPasswordToken forgotPasswordToken =forgotPasswordService.findById(id);


        boolean isVerified = forgotPasswordToken.getOtp().equals(req.getOtp());

        if(isVerified){
            userService.updatePassword(forgotPasswordToken.getUser(), req.getPassword());
            APIResponse res=new APIResponse();
            res.setMessage("Password Updated Successfully");



            return new ResponseEntity<APIResponse>(res, HttpStatus.OK);

        }

        throw new Exception("Wrong OTP");

    }

}
