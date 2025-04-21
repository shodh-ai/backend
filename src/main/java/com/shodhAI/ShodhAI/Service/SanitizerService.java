package com.shodhAI.ShodhAI.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SanitizerService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*(select|insert|update|delete|union|drop|exec|create|alter|truncate|--|\\b'or\\b|\\b1=1\\b|\\b'\\s*or\\b|\\b'\\s*and\\b).*",
            Pattern.CASE_INSENSITIVE
    );

    /*private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            ".*\\b(select|insert|update|delete|union|drop|exec|create|alter|truncate)\\b.*",
            Pattern.CASE_INSENSITIVE
    );*/

    private static final Pattern FILE_INCLUSION_PATTERN = Pattern.compile(
            ".*(file://|php://|ftp://|http://|https://|jar://|zip://|dict://|gopher://|ws://|wss://|mailto:|data:|javascript:).*",
            Pattern.CASE_INSENSITIVE
    );


    private static final Pattern HTML_INJECTION_PATTERN = Pattern.compile(
            ".*(<[^>]*>).*",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern XSS_PATTERN = Pattern.compile(
            ".*(<script|<img|onerror|javascript:|data:text/html|<iframe|<object|<embed).*",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
            ".*(cmd.exe|bash|sh|exec|system|\\$\\(|\\;|\\||\\&|rm\\s+-rf|ls).*",
            Pattern.CASE_INSENSITIVE
    );

    public void sanitizeInputMap(Object input) throws Exception {
        try {
            Map<String, Object> inputMap = objectMapper.convertValue(input, new TypeReference<Map<String, Object>>() {
            });
            inputMap = removeKeyValuePair(inputMap);
            Map<String, Object> sanitizedDataMap = new HashMap<>();

            for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                sanitizeValue(key, value); // new method to handle logic
//                sanitizedDataMap.put(key, sanitizedValue);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    private Object sanitizeValue(String key, Object value) throws Exception {
        try {
            if (value instanceof String strVal) {
                // If the key indicates it's sensitive (like password, token, etc.), mask it
                if (key.matches("(?i).*password.*|.*secret.*|.*token.*|.*key.*")) {
                    return "******"; // masked
                }

                // Otherwise, clean the string
                return strVal
                        .replaceAll("<[^>]*>", "") // remove HTML tags
                        .replaceAll("[\\n\\r\\t]", "") // remove line breaks and tabs
                        .trim();
            }

            // If it's not a string, just return the original
            return value;

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException("Unable to sanitize the input");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Map<String, Object> removeKeyValuePair(Map<String, Object> inputData) throws Exception {
        try {
            Map<String, Object> cleanedData = new HashMap<>(inputData);
            Iterator<Map.Entry<String, Object>> iterator = cleanedData.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value != null && isMalicious(value.toString())) {
                    iterator.remove();
                    throw new IllegalArgumentException("Suspicious data found");
                }
            }

            return cleanedData;
        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

    public boolean isMalicious(String value) {
        boolean isMalicious = false;

        if (SQL_INJECTION_PATTERN.matcher(value).find()) {
            isMalicious = true;
            log.info("SQL Injection detected");
        }
        if (XSS_PATTERN.matcher(value).find()) {
            isMalicious = true;
            log.info("XSS detected");
        }
        if (COMMAND_INJECTION_PATTERN.matcher(value).find()) {
            isMalicious = true;
            log.info("Command Injection detected");
        }

        if (FILE_INCLUSION_PATTERN.matcher(value).find()) {
            isMalicious = true;
        }
        if (HTML_INJECTION_PATTERN.matcher(value).find()) {
            isMalicious = true;
        }

        return isMalicious;
    }

}

