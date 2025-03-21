package com.shodhAI.ShodhAI.Service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_DURATION_MS = TimeUnit.MINUTES.toMillis(5);
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, OtpData> otpStorage = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", secureRandom.nextInt(1000000));
        otpStorage.put(email, new OtpData(otp, System.currentTimeMillis() + OTP_EXPIRY_DURATION_MS));
        return otp;
    }

    public boolean validateOtp(String email, String inputOtp) {
        OtpData otpData = otpStorage.get(email);
        if (otpData != null && System.currentTimeMillis() < otpData.expiryTime) {
            if (otpData.otp.equals(inputOtp)) {
                otpStorage.remove(email); // Remove OTP after successful validation
                return true;
            }
        }
        return false;
    }

    private static class OtpData {
        String otp;
        long expiryTime;

        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}

