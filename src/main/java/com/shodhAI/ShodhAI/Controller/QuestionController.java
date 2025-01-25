package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Dto.QuestionRequestDto;
import com.shodhAI.ShodhAI.Dto.QuestionResponseDto;
import com.shodhAI.ShodhAI.Dto.QuestionResponseWrapper;
import com.shodhAI.ShodhAI.Entity.Hint;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Question;
import com.shodhAI.ShodhAI.Entity.Topic;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.QuestionService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/question", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class QuestionController {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    QuestionService questionService;

    @PostMapping("/generate_questions")
    public ResponseEntity<?> generateQuestions(@Valid @RequestBody QuestionRequestDto questionRequestDto) {
        try {

            questionService.validateQuestion(questionRequestDto);
            Module module = questionService.validateModule(questionRequestDto);
            Topic topic = questionService.validateTopic(questionRequestDto);
            // Now its time to call the api from ml/ai and get response

            List<QuestionResponseDto> questionResponseDtoList = new ArrayList<>();

            List<Question> questionList = new ArrayList<>();

            for(QuestionResponseDto questionResponseDto: questionResponseDtoList) {
                Question question = new Question();
                List<Hint> hints = new ArrayList<>();
                for(String string: questionResponseDto.getHints()) {
                    Hint hint = new Hint();
                    hint.setLevel("basic"); // TODO can further use in case of different hint level like basic, moderate or advanced.
                    hint.setText(hint.getText());
                }
                question.setHints(hints);
                questionService.saveQuestion(questionResponseDto, question);
            }

            QuestionResponseWrapper questionResponseWrapper = new QuestionResponseWrapper();
            questionResponseWrapper.wrapDetails(questionResponseDtoList, questionRequestDto.getQuestionMaterial(), topic, module);

            return ResponseService.generateSuccessResponse("Question Created Successfully", questionResponseWrapper, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
