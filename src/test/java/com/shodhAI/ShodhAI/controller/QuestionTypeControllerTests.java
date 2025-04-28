package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.QuestionTypeController;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Service.QuestionTypeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import com.shodhAI.ShodhAI.Service.StudentService;
import com.shodhAI.ShodhAI.configuration.TestJwtConfig;
import com.shodhAI.ShodhAI.configuration.TestSecurityConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(QuestionTypeController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class QuestionTypeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private QuestionTypeService questionTypeService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllQuestionTypes")
    void testGetAllQuestionTypes() throws Exception {
        // Create sample question types
        List<QuestionType> questionTypes = new ArrayList<>();
        QuestionType type1 = new QuestionType();
        type1.setQuestionTypeId(1L);
        type1.setQuestionType("Article");

        QuestionType type2 = new QuestionType();
        type2.setQuestionTypeId(2L);
        type2.setQuestionType("Video");

        questionTypes.add(type1);
        questionTypes.add(type2);

        // Mock service call
        when(questionTypeService.questionTypeFilter()).thenReturn(questionTypes);

        // Perform GET request
        mockMvc.perform(get("/question-type/get-filter-question-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Question Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(questionTypeService).questionTypeFilter();
    }

    @Test
    @DisplayName("testGetAllQuestionTypesEmptyList")
    void testGetAllQuestionTypesEmptyList() throws Exception {
        // Mock empty list response
        when(questionTypeService.questionTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/question-type/get-filter-question-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No question types found"));

        // Verify service method was called
        verify(questionTypeService).questionTypeFilter();
    }

    @Test
    @DisplayName("testGetQuestionTypeByQuestionTypeId")
    void testGetQuestionTypeByQuestionTypeId() throws Exception {
        // Create sample question type
        QuestionType questionType = new QuestionType();
        questionType.setQuestionTypeId(1L);
        questionType.setQuestionType("Article");

        // Mock service call
        when(questionTypeService.getQuestionTypeById(1L)).thenReturn(questionType);

        // Perform GET request
        mockMvc.perform(get("/question-type/get-question-type-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Question Type Retrieved Successfully"))
                .andExpect(jsonPath("$.data.question_type_id").value(1L));


        // Verify service method was called
        verify(questionTypeService).getQuestionTypeById(1L);
    }

    @Test
    @DisplayName("testGetQuestionTypeByQuestionTypeIdNotFound")
    void testGetQuestionTypeByQuestionTypeIdNotFound() throws Exception {
        // Mock not found scenario
        when(questionTypeService.getQuestionTypeById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/question-type/get-question-type-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(questionTypeService).getQuestionTypeById(999L);
    }

    @Test
    @DisplayName("testAddQuestionType")
    void testAddQuestionType() throws Exception {
        // Create a sample QuestionType
        QuestionType questionType = new QuestionType();
        questionType.setQuestionType("Article");

        QuestionType savedQuestionType = new QuestionType();
        savedQuestionType.setQuestionTypeId(1L);
        savedQuestionType.setQuestionType("Article");

        // Mock service call
        when(questionTypeService.addQuestionType(any(QuestionType.class))).thenReturn(savedQuestionType);

        // Perform POST request
        mockMvc.perform(post("/question-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(questionType)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Question type is successfully added"))
                .andExpect(jsonPath("$.data.question_type_id").value(1L));

        // Verify service method was called
        verify(questionTypeService).addQuestionType(any(QuestionType.class));
    }

    @Test
    @DisplayName("testAddQuestionTypeIllegalArgumentException")
    void testAddQuestionTypeIllegalArgumentException() throws Exception {
        // Create a sample QuestionType
        QuestionType questionType = new QuestionType();
        questionType.setQuestionType(""); // Invalid name

        // Mock service exception
        when(questionTypeService.addQuestionType(any(QuestionType.class)))
                .thenThrow(new IllegalArgumentException("Question type name cannot be empty"));

        // Perform POST request
        mockMvc.perform(post("/question-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(questionType)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Question type name cannot be empty"));

        // Verify exception handling
        verify(questionTypeService).addQuestionType(any(QuestionType.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testUpdateQuestionType")
    void testUpdateQuestionType() throws Exception {
        // Create a sample QuestionType for update
        QuestionType questionType = new QuestionType();
        questionType.setQuestionType("Updated Article");

        QuestionType existingQuestionType = new QuestionType();
        existingQuestionType.setQuestionTypeId(1L);
        existingQuestionType.setQuestionType("Article");

        QuestionType updatedQuestionType = new QuestionType();
        updatedQuestionType.setQuestionTypeId(1L);
        updatedQuestionType.setQuestionType("Updated Article");

        // Mock service calls
        when(questionTypeService.getQuestionTypeById(1L)).thenReturn(existingQuestionType);
        when(questionTypeService.updateQuestionType(eq(1L), any(QuestionType.class))).thenReturn(updatedQuestionType);

        // Perform PATCH request
        mockMvc.perform(patch("/question-type/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(questionType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Question type is updated successfully "))
                .andExpect(jsonPath("$.data.question_type_id").value(1L))
                .andExpect(jsonPath("$.data.question_type").value("Updated Article"));

        // Verify service methods were called
        verify(questionTypeService).getQuestionTypeById(1L);
        verify(questionTypeService).updateQuestionType(eq(1L), any(QuestionType.class));
    }

    @Test
    @DisplayName("testUpdateQuestionTypeNotFound")
    void testUpdateQuestionTypeNotFound() throws Exception {
        // Create a sample QuestionType for update
        QuestionType questionType = new QuestionType();
        questionType.setQuestionType("Updated Article");

        // Mock service call - Question type not found
        when(questionTypeService.getQuestionTypeById(999L)).thenReturn(null);

        // Perform PATCH request
        mockMvc.perform(patch("/question-type/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(questionType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(questionTypeService).getQuestionTypeById(999L);
        verify(questionTypeService, never()).updateQuestionType(eq(999L), any(QuestionType.class));
    }

    @Test
    @DisplayName("testDeleteQuestionType")
    void testDeleteQuestionType() throws Exception {
        // Create sample question type
        QuestionType questionType = new QuestionType();
        questionType.setQuestionTypeId(1L);
        questionType.setQuestionType("Article");

        // Mock service calls
        when(questionTypeService.getQuestionTypeById(1L)).thenReturn(questionType);
        when(questionTypeService.deleteQuestionTypeById(1L)).thenReturn(questionType);

        // Perform DELETE request
        mockMvc.perform(delete("/question-type/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Question type is archived successfully"))
                .andExpect(jsonPath("$.data.question_type_id").value(1L));

        // Verify service methods were called
        verify(questionTypeService).getQuestionTypeById(1L);
        verify(questionTypeService).deleteQuestionTypeById(1L);
    }

    @Test
    @DisplayName("testDeleteQuestionTypeNotFound")
    void testDeleteQuestionTypeNotFound() throws Exception {
        // Mock not found scenario
        when(questionTypeService.getQuestionTypeById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/question-type/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service methods called
        verify(questionTypeService).getQuestionTypeById(999L);
        verify(questionTypeService, never()).deleteQuestionTypeById(999L);
    }

    @Test
    @DisplayName("testGetFilterQuestionTypes")
    void testGetFilterQuestionTypes() throws Exception {
        // Create sample question types
        List<QuestionType> questionTypes = new ArrayList<>();
        QuestionType type1 = new QuestionType();
        type1.setQuestionTypeId(1L);
        type1.setQuestionType("Article");

        QuestionType type2 = new QuestionType();
        type2.setQuestionTypeId(2L);
        type2.setQuestionType("Video");

        questionTypes.add(type1);
        questionTypes.add(type2);

        // Mock service call
        when(questionTypeService.questionTypeFilter()).thenReturn(questionTypes);

        // Perform GET request
        mockMvc.perform(get("/question-type/get-filter-question-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Question Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(questionTypeService).questionTypeFilter();
    }

    @Test
    @DisplayName("testGetFilterQuestionTypesEmptyList")
    void testGetFilterQuestionTypesEmptyList() throws Exception {
        // Mock empty list response
        when(questionTypeService.questionTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/question-type/get-filter-question-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No question types found"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service method was called
        verify(questionTypeService).questionTypeFilter();
    }
}
