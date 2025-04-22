package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.DoubtController;
import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import com.shodhAI.ShodhAI.Service.DoubtService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(DoubtController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class DoubtLevelControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoubtService doubtLevelService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllDoubtLevels")
    void testGetAllDoubtLevels() throws Exception {
        // Create sample content types
        List<DoubtLevel> doubtLevels = new ArrayList<>();
        DoubtLevel type1 = new DoubtLevel();
        type1.setDoubtLevelId(1L);
        type1.setDoubtLevel("Article");

        DoubtLevel type2 = new DoubtLevel();
        type2.setDoubtLevelId(2L);
        type2.setDoubtLevel("Video");

        doubtLevels.add(type1);
        doubtLevels.add(type2);

        // Mock service call
        when(doubtLevelService.getAllDoubtLevels()).thenReturn(doubtLevels);

        // Perform GET request
        mockMvc.perform(get("/doubt/get-all-doubt-level")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Doubt Level Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(doubtLevelService).getAllDoubtLevels();
    }

    @Test
    @DisplayName("testGetAllDoubtLevelsEmptyList")
    void testGetAllDoubtLevelsEmptyList() throws Exception {
        // Mock empty list response
        when(doubtLevelService.getAllDoubtLevels()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/doubt/get-all-doubt-level")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(doubtLevelService).getAllDoubtLevels();
    }

    @Test
    @DisplayName("testGetDoubtLevelById")
    void testGetDoubtLevelById() throws Exception {
        // Create sample content type
        DoubtLevel doubtLevel = new DoubtLevel();
        doubtLevel.setDoubtLevelId(1L);
        doubtLevel.setDoubtLevel("Article");

        // Mock service call
        when(doubtLevelService.getDoubtLevelById(1L)).thenReturn(doubtLevel);

        // Perform GET request
        mockMvc.perform(get("/doubt/get-doubt-level-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Doubt Level Retrieved Successfully"))
                .andExpect(jsonPath("$.data.doubt_level_id").value(1L));


        // Verify service method was called
        verify(doubtLevelService).getDoubtLevelById(1L);
    }

    @Test
    @DisplayName("testGetDoubtLevelByIdNotFound")
    void testGetDoubtLevelByIdNotFound() throws Exception {
        // Mock not found scenario
        when(doubtLevelService.getDoubtLevelById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/doubt/get-doubt-level-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(doubtLevelService).getDoubtLevelById(999L);
    }

    @Test
    @DisplayName("testDeleteDoubtLevel")
    void testDeleteDoubtLevel() throws Exception {
        // Create sample content type
        DoubtLevel doubtLevel = new DoubtLevel();
        doubtLevel.setDoubtLevelId(1L);
        doubtLevel.setDoubtLevel("Article");

        // Mock service calls
        when(doubtLevelService.getDoubtLevelById(1L)).thenReturn(doubtLevel);
        when(doubtLevelService.deleteDoubtLevelById(1L)).thenReturn(doubtLevel);

        // Perform DELETE request
        mockMvc.perform(delete("/doubt/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Doubt Level is archived successfully"))
                .andExpect(jsonPath("$.data.doubt_level_id").value(1L));

        // Verify service methods were called
        verify(doubtLevelService).getDoubtLevelById(1L);
        verify(doubtLevelService).deleteDoubtLevelById(1L);
    }

    @Test
    @DisplayName("testDeleteDoubtLevelNotFound")
    void testDeleteDoubtLevelNotFound() throws Exception {
        // Mock not found scenario
        when(doubtLevelService.getDoubtLevelById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/doubt/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service methods called
        verify(doubtLevelService).getDoubtLevelById(999L);
        verify(doubtLevelService, never()).deleteDoubtLevelById(999L);
    }
}