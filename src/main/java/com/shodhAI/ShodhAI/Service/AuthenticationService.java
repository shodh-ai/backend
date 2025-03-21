package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.ForgotPasswordDto;
import com.shodhAI.ShodhAI.Dto.SignUpDto;
import com.shodhAI.ShodhAI.Dto.VerifyOtpDto;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    EntityManager entityManager;

    public void validateSignUp(SignUpDto signUpDto) throws Exception {
        try {

            if(signUpDto.getEmail() == null || signUpDto.getEmail().isEmpty() || !signUpDto.getEmail().endsWith(".com")) {
                throw new IllegalArgumentException("Email cannot be null or empty or having invalid format(***@**.com)");
            }

            if(signUpDto.getRoleId() == null || signUpDto.getRoleId() <= 0) {
                throw new IllegalArgumentException("Role Id cannot be null or <= 0");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public void validateVerifyOtp(VerifyOtpDto verifyOtpDto) throws Exception {
        try {

            if(verifyOtpDto.getEmail() == null || verifyOtpDto.getEmail().isEmpty() || !verifyOtpDto.getEmail().endsWith(".com")) {
                throw new IllegalArgumentException("Email cannot be null or empty or having invalid format(***@**.com)");
            }

            if(verifyOtpDto.getRoleId() == null || verifyOtpDto.getRoleId() <= 0) {
                throw new IllegalArgumentException("Role Id cannot be null or <= 0");
            }

            if(verifyOtpDto.getOtp().length() != 6 ) {
                throw new IllegalArgumentException("Otp length must be of 6 digits");
            }
            Long otp = Long.parseLong(verifyOtpDto.getOtp());

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public void validateForgotPasswordDto(ForgotPasswordDto forgotPasswordDto) throws Exception {
        try {

            if(forgotPasswordDto.getNewPassword() == null || forgotPasswordDto.getNewPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty and length must be > 8");
            }

            /*if(forgotPasswordDto.getConfirmPassword() == null || forgotPasswordDto.getConfirmPassword().isEmpty() || forgotPasswordDto.getConfirmPassword().trim().length() <= 8) {
                throw new IllegalArgumentException("Confirm Password cannot be null or empty and length must be > 8");
            }
            forgotPasswordDto.setConfirmPassword(forgotPasswordDto.getConfirmPassword().trim());*/

            if(!forgotPasswordDto.getNewPassword().equals(forgotPasswordDto.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords must match each other");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

}
