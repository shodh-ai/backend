package com.shodhAI.ShodhAI.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SanitizerService {

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

    public Map<String, Object> sanitizeInputMap(Map<String, Object> inputMap) {
        inputMap=removeKeyValuePair(inputMap);
        Map<String, Object> sanitizedDataMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object sanitizedValue = sanitizeValue(key, value); // new method to handle logic
            sanitizedDataMap.put(key, sanitizedValue);
        }
        return sanitizedDataMap;
    }

    private Object sanitizeValue(String key, Object value) {
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
    }

    public Map<String, Object> removeKeyValuePair(Map<String, Object> inputData) {
        Map<String, Object> cleanedData = new HashMap<>(inputData);
        Iterator<Map.Entry<String, Object>> iterator = cleanedData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value != null && isMalicious(value.toString())) {
                iterator.remove();
            }
        }

        return cleanedData;
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

