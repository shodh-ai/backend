package com.shodhAI.ShodhAI.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ExceptionHandlingService {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingService.class);

    public void handleHttpError(ResponseEntity<String> response) {

        HttpStatusCode statusCode = response.getStatusCode();
        String responseBody = response.getBody();
        throw new RuntimeException("HTTP Error: " + statusCode + ", Response Body: " + responseBody);
    }

    public String handleHttpClientErrorException(HttpClientErrorException e) {

        HttpStatusCode statusCode = e.getStatusCode();
        String responseBody = e.getResponseBodyAsString();
        throw new RuntimeException("HTTP Client Error: " + statusCode + ", Response Body: " + responseBody, e);
    }

    public String handleException(Exception e) {

        logger.error("Exception occurred: ", e);
        if (e instanceof HttpClientErrorException) {
            return handleHttpClientErrorException((HttpClientErrorException) e);
        } else {
            return "Something went wrong: " + e.getMessage();
        }
    }

    public String handleException(HttpStatus status, Exception e) {

        if (status.equals(HttpStatus.BAD_REQUEST)) {
            return status + " " + e.getMessage();
        } else if (status.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            return status + " " + e.getMessage();
        } else {
            return "Something went wrong: " + e.getMessage();
        }
    }
}
