package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.TopicTypeController;
import com.shodhAI.ShodhAI.Entity.TopicType;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import com.shodhAI.ShodhAI.Service.TopicTypeService;
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
@WebMvcTest(TopicTypeController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class TopicTypeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicTypeService topicTypeService;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllTopicTypes")
    void testGetAllTopicTypes() throws Exception {
        // Create sample topic types
        List<TopicType> topicTypes = new ArrayList<>();
        TopicType type1 = new TopicType();
        type1.setTopicTypeId(1L);
        type1.setTopicTypeName("Article");

        TopicType type2 = new TopicType();
        type2.setTopicTypeId(2L);
        type2.setTopicTypeName("Video");

        topicTypes.add(type1);
        topicTypes.add(type2);

        // Mock service call
        when(topicTypeService.topicTypeFilter()).thenReturn(topicTypes);

        // Perform GET request
        mockMvc.perform(get("/topic-type/get-filter-topic-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Topic Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(topicTypeService).topicTypeFilter();
    }

    @Test
    @DisplayName("testGetAllTopicTypesEmptyList")
    void testGetAllTopicTypesEmptyList() throws Exception {
        // Mock empty list response
        when(topicTypeService.topicTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/topic-type/get-filter-topic-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No topic types found"));

        // Verify service method was called
        verify(topicTypeService).topicTypeFilter();
    }

    @Test
    @DisplayName("testGetTopicTypeById")
    void testGetTopicTypeById() throws Exception {
        // Create sample topic type
        TopicType topicType = new TopicType();
        topicType.setTopicTypeId(1L);
        topicType.setTopicTypeName("Article");

        // Mock service call
        when(topicTypeService.getTopicTypeById(1L)).thenReturn(topicType);

        // Perform GET request
        mockMvc.perform(get("/topic-type/get-topic-type-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Topic Type Retrieved Successfully"))
                .andExpect(jsonPath("$.data.topic_type_id").value(1L));


        // Verify service method was called
        verify(topicTypeService).getTopicTypeById(1L);
    }

    @Test
    @DisplayName("testGetTopicTypeByIdNotFound")
    void testGetTopicTypeByIdNotFound() throws Exception {
        // Mock not found scenario
        when(topicTypeService.getTopicTypeById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/topic-type/get-topic-type-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(topicTypeService).getTopicTypeById(999L);
    }

    @Test
    @DisplayName("testAddTopicType")
    void testAddTopicType() throws Exception {
        // Create a sample TopicType
        TopicType topicType = new TopicType();
        topicType.setTopicTypeName("Article");

        TopicType savedTopicType = new TopicType();
        savedTopicType.setTopicTypeId(1L);
        savedTopicType.setTopicTypeName("Article");

        // Mock service call
        when(topicTypeService.addTopicType(any(TopicType.class))).thenReturn(savedTopicType);

        // Perform POST request
        mockMvc.perform(post("/topic-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(topicType)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Topic Type is successfully added"))
                .andExpect(jsonPath("$.data.topic_type_id").value(1L));

        // Verify service method was called
        verify(topicTypeService).addTopicType(any(TopicType.class));
    }

    @Test
    @DisplayName("testAddTopicTypeIllegalArgumentException")
    void testAddTopicTypeIllegalArgumentException() throws Exception {
        // Create a sample TopicType
        TopicType topicType = new TopicType();
        topicType.setTopicTypeName(""); // Invalid name

        // Mock service exception
        when(topicTypeService.addTopicType(any(TopicType.class)))
                .thenThrow(new IllegalArgumentException("Topic type name cannot be empty"));

        // Perform POST request
        mockMvc.perform(post("/topic-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(topicType)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Topic type name cannot be empty"));

        // Verify exception handling
        verify(topicTypeService).addTopicType(any(TopicType.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testUpdateTopicType")
    void testUpdateTopicType() throws Exception {
        // Create a sample TopicType for update
        TopicType topicType = new TopicType();
        topicType.setTopicTypeName("Updated Article");

        TopicType existingTopicType = new TopicType();
        existingTopicType.setTopicTypeId(1L);
        existingTopicType.setTopicTypeName("Article");

        TopicType updatedTopicType = new TopicType();
        updatedTopicType.setTopicTypeId(1L);
        updatedTopicType.setTopicTypeName("Updated Article");

        // Mock service calls
        when(topicTypeService.getTopicTypeById(1L)).thenReturn(existingTopicType);
        when(topicTypeService.updateTopicType(eq(1L), any(TopicType.class))).thenReturn(updatedTopicType);

        // Perform PATCH request
        mockMvc.perform(patch("/topic-type/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(topicType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Topic type is updated successfully "))
                .andExpect(jsonPath("$.data.topic_type_id").value(1L))
                .andExpect(jsonPath("$.data.topic_type_name").value("Updated Article"));

        // Verify service methods were called
        verify(topicTypeService).getTopicTypeById(1L);
        verify(topicTypeService).updateTopicType(eq(1L), any(TopicType.class));
    }

    @Test
    @DisplayName("testUpdateTopicTypeNotFound")
    void testUpdateTopicTypeNotFound() throws Exception {
        // Create a sample TopicType for update
        TopicType topicType = new TopicType();
        topicType.setTopicTypeName("Updated Article");

        // Mock service call - Topic type not found
        when(topicTypeService.getTopicTypeById(999L)).thenReturn(null);

        // Perform PATCH request
        mockMvc.perform(patch("/topic-type/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(topicType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(topicTypeService).getTopicTypeById(999L);
        verify(topicTypeService, never()).updateTopicType(eq(999L), any(TopicType.class));
    }

    @Test
    @DisplayName("testDeleteTopicType")
    void testDeleteTopicType() throws Exception {
        // Create sample topic type
        TopicType topicType = new TopicType();
        topicType.setTopicTypeId(1L);
        topicType.setTopicTypeName("Article");

        // Mock service calls
        when(topicTypeService.getTopicTypeById(1L)).thenReturn(topicType);
        when(topicTypeService.deleteTopicTypeById(1L)).thenReturn(topicType);

        // Perform DELETE request
        mockMvc.perform(delete("/topic-type/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Topic type is archived successfully"))
                .andExpect(jsonPath("$.data.topic_type_id").value(1L));

        // Verify service methods were called
        verify(topicTypeService).getTopicTypeById(1L);
        verify(topicTypeService).deleteTopicTypeById(1L);
    }

    @Test
    @DisplayName("testDeleteTopicTypeNotFound")
    void testDeleteTopicTypeNotFound() throws Exception {
        // Mock not found scenario
        when(topicTypeService.getTopicTypeById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/topic-type/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service methods called
        verify(topicTypeService).getTopicTypeById(999L);
        verify(topicTypeService, never()).deleteTopicTypeById(999L);
    }

    @Test
    @DisplayName("testGetFilterTopicTypes")
    void testGetFilterTopicTypes() throws Exception {
        // Create sample topic types
        List<TopicType> topicTypes = new ArrayList<>();
        TopicType type1 = new TopicType();
        type1.setTopicTypeId(1L);
        type1.setTopicTypeName("Article");

        TopicType type2 = new TopicType();
        type2.setTopicTypeId(2L);
        type2.setTopicTypeName("Video");

        topicTypes.add(type1);
        topicTypes.add(type2);

        // Mock service call
        when(topicTypeService.topicTypeFilter()).thenReturn(topicTypes);

        // Perform GET request
        mockMvc.perform(get("/topic-type/get-filter-topic-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Topic Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(topicTypeService).topicTypeFilter();
    }

    @Test
    @DisplayName("testGetFilterTopicTypesEmptyList")
    void testGetFilterTopicTypesEmptyList() throws Exception {
        // Mock empty list response
        when(topicTypeService.topicTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/topic-type/get-filter-topic-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No topic types found"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service method was called
        verify(topicTypeService).topicTypeFilter();
    }
}
