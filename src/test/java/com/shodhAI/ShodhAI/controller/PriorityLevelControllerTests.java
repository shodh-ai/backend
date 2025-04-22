package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.PriorityLevelController;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Service.PriorityLevelService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(PriorityLevelController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class PriorityLevelControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriorityLevelService priorityLevelService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllPriorityLevels")
    void testGetAllPriorityLevels() throws Exception {
        // Create sample content types
        List<PriorityLevel> priorityLevels = new ArrayList<>();
        PriorityLevel type1 = new PriorityLevel();
        type1.setPriorityLevelId(1L);
        type1.setPriorityLevel("Article");

        PriorityLevel type2 = new PriorityLevel();
        type2.setPriorityLevelId(2L);
        type2.setPriorityLevel("Video");

        priorityLevels.add(type1);
        priorityLevels.add(type2);

        // Mock service call
        when(priorityLevelService.getAllPriorityLevels()).thenReturn(priorityLevels);

        // Perform GET request
        mockMvc.perform(get("/priority-level/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Priority Level Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(priorityLevelService).getAllPriorityLevels();
    }

    @Test
    @DisplayName("testGetAllPriorityLevelsEmptyList")
    void testGetAllPriorityLevelsEmptyList() throws Exception {
        // Mock empty list response
        when(priorityLevelService.getAllPriorityLevels()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/priority-level/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(priorityLevelService).getAllPriorityLevels();
    }

    @Test
    @DisplayName("testGetPriorityLevelById")
    void testGetPriorityLevelById() throws Exception {
        // Create sample content type
        PriorityLevel priorityLevel = new PriorityLevel();
        priorityLevel.setPriorityLevelId(1L);
        priorityLevel.setPriorityLevel("Article");

        // Mock service call
        when(priorityLevelService.getPriorityLevelById(1L)).thenReturn(priorityLevel);

        // Perform GET request
        mockMvc.perform(get("/priority-level/get-priority-level-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Priority Level Retrieved Successfully"))
                .andExpect(jsonPath("$.data.priority_level_id").value(1L));


        // Verify service method was called
        verify(priorityLevelService).getPriorityLevelById(1L);
    }

    @Test
    @DisplayName("testGetPriorityLevelByIdNotFound")
    void testGetPriorityLevelByIdNotFound() throws Exception {
        // Mock not found scenario
        when(priorityLevelService.getPriorityLevelById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/priority-level/get-priority-level-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(priorityLevelService).getPriorityLevelById(999L);
    }

    @Test
    @DisplayName("testDeletePriorityLevel")
    void testDeletePriorityLevel() throws Exception {
        // Create sample content type
        PriorityLevel priorityLevel = new PriorityLevel();
        priorityLevel.setPriorityLevelId(1L);
        priorityLevel.setPriorityLevel("Article");

        // Mock service calls
        when(priorityLevelService.getPriorityLevelById(1L)).thenReturn(priorityLevel);
        when(priorityLevelService.deletePriorityLevelById(1L)).thenReturn(priorityLevel);

        // Perform DELETE request
        mockMvc.perform(delete("/priority-level/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Priority level is archived successfully"))
                .andExpect(jsonPath("$.data.priority_level_id").value(1L));

        // Verify service methods were called
        verify(priorityLevelService).getPriorityLevelById(1L);
        verify(priorityLevelService).deletePriorityLevelById(1L);
    }

    @Test
    @DisplayName("testDeletePriorityLevelNotFound")
    void testDeletePriorityLevelNotFound() throws Exception {
        // Mock not found scenario
        when(priorityLevelService.getPriorityLevelById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/priority-level/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service methods called
        verify(priorityLevelService).getPriorityLevelById(999L);
        verify(priorityLevelService, never()).deletePriorityLevelById(999L);
    }
}