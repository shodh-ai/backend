package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Dto.QuestionListWrapper;
import com.shodhAI.ShodhAI.Dto.QuestionResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${ml.api.url}")  // Inject the URL from application.properties
    private String mlApiUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    public List<QuestionResponseDto> callAIToGenerateQuestions(Map<String, Object> requestPayload) throws Exception {
        try {
            String url = mlApiUrl + "/question";

            // Use ParameterizedTypeReference to specify the response type
            ParameterizedTypeReference<QuestionListWrapper> typeRef =
                    new ParameterizedTypeReference<>() {
                    };

            // Making the request and receiving a wrapper that contains the question list
            ResponseEntity<QuestionListWrapper> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(requestPayload),
                    typeRef
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Extract the list of questions from the wrapper object
                System.out.println("YHA BHI ISSUE LG RHA H: " + responseEntity.getBody().toString());
                return responseEntity.getBody().getQuestionList();
            } else {
                throw new RuntimeException("AI Service error: " + responseEntity.getStatusCode());
            }

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }

    }

}
