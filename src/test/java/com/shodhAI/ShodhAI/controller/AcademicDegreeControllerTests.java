package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Component.TokenBlacklist;
import com.shodhAI.ShodhAI.Controller.AcademicDegreeController;
import com.shodhAI.ShodhAI.Dto.AcademicDegreeDto;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
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
@WebMvcTest(AcademicDegreeController.class)
//@WebMvcTest(EmployeeController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class AcademicDegreeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private AcademicDegreeService academicDegreeService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    private String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    @Test
    @DisplayName("test_addAcademicDegree_success")
    void testAddAcademicDegree() throws Exception {
        // Create a sample AcademicDegreeDto
        AcademicDegreeDto academicDegreeDto = new AcademicDegreeDto();
        academicDegreeDto.setDegreeName("Bachelor of Science");
        academicDegreeDto.setProgramName("Computer Science Degree");

        AcademicDegree academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(1L);
        academicDegree.setDegreeName("Bachelor of Science");
        academicDegree.setProgramName("Computer Science Degree");

        // Mock service calls
        doNothing().when(academicDegreeService).validateAcademicDegree(any(AcademicDegreeDto.class));
        when(academicDegreeService.saveAcademicDegree(any(AcademicDegreeDto.class))).thenReturn(academicDegree);

        // Perform POST request
        mockMvc.perform(post("/academic-degree/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(academicDegreeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Academic Degree Created Successfully"))
                .andExpect(jsonPath("$.data.degreeId").value(1L))
                .andExpect(jsonPath("$.data.degree_name").value("Bachelor of Science"));

        // Verify service methods were called
        verify(academicDegreeService).validateAcademicDegree(any(AcademicDegreeDto.class));
        verify(academicDegreeService).saveAcademicDegree(any(AcademicDegreeDto.class));
    }

    @Test
    @DisplayName("test_addAcademicDegree_validationError")
    void testAddAcademicDegreeValidationError() throws Exception {
        // Create a sample AcademicDegreeDto
        AcademicDegreeDto academicDegreeDto = new AcademicDegreeDto();
        academicDegreeDto.setDegreeName(""); // Invalid empty name

        // Mock validation exception
        doThrow(new IllegalArgumentException("Name cannot be empty"))
                .when(academicDegreeService).validateAcademicDegree(any(AcademicDegreeDto.class));

        // Perform POST request
        mockMvc.perform(post("/academic-degree/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(academicDegreeDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Illegal Exception Caught: Name cannot be empty"));

        // Verify service methods were called correctly
        verify(academicDegreeService).validateAcademicDegree(any(AcademicDegreeDto.class));
        verify(academicDegreeService, never()).saveAcademicDegree(any(AcademicDegreeDto.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("test_getAllAcademicDegrees_success")
    void testGetAllAcademicDegrees() throws Exception {
        // Create sample academic degrees
        List<AcademicDegree> academicDegrees = new ArrayList<>();
        AcademicDegree degree1 = new AcademicDegree();
        degree1.setDegreeId(1L);
        degree1.setDegreeName("Bachelor of Science");

        AcademicDegree degree2 = new AcademicDegree();
        degree2.setDegreeId(2L);
        degree2.setDegreeName("Master of Science");

        academicDegrees.add(degree1);
        academicDegrees.add(degree2);

        // Mock service call
        when(academicDegreeService.getAllAcademicDegree()).thenReturn(academicDegrees);

        // Perform GET request
        mockMvc.perform(get("/academic-degree/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Academic Degree Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(academicDegreeService).getAllAcademicDegree();
    }

    @Test
    @DisplayName("test_getAllAcademicDegrees_emptyList")
    void testGetAllAcademicDegreesEmptyList() throws Exception {
        // Mock empty list response
        when(academicDegreeService.getAllAcademicDegree()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/academic-degree/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(academicDegreeService).getAllAcademicDegree();
    }

    @Test
    @DisplayName("test_getAcademicDegreeById_success")
    void testGetAcademicDegreeById() throws Exception {
        // Create sample academic degree
        AcademicDegree academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(1L);
        academicDegree.setDegreeName("Bachelor of Science");
        academicDegree.setProgramName("Computer Science Degree");

        // Mock service call
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);

        // Perform GET request
        mockMvc.perform(get("/academic-degree/get-academic-degree-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Academic Degree Retrieved Successfully"))
                .andExpect(jsonPath("$.data.degreeId").value(1))
                .andExpect(jsonPath("$.data.degree_name").value("Bachelor of Science"));

        // Verify service method was called
        verify(academicDegreeService).getAcademicDegreeById(1L);
    }

    @Test
    @DisplayName("test_getAcademicDegreeById_notFound")
    void testGetAcademicDegreeByIdNotFound() throws Exception {
        // Mock not found scenario
        when(academicDegreeService.getAcademicDegreeById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/academic-degree/get-academic-degree-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(academicDegreeService).getAcademicDegreeById(999L);
    }

    @Test
    @DisplayName("test_updateAcademicDegree_success")
    void testUpdateAcademicDegree() throws Exception {
        // Create sample DTO and entity
        AcademicDegreeDto academicDegreeDto = new AcademicDegreeDto();
        academicDegreeDto.setDegreeName("Updated Bachelor");
        academicDegreeDto.setProgramName("Updated Description");

        AcademicDegree existingDegree = new AcademicDegree();
        existingDegree.setDegreeId(1L);
        existingDegree.setDegreeName("Bachelor of Science");

        AcademicDegree updatedDegree = new AcademicDegree();
        updatedDegree.setDegreeId(1L);
        updatedDegree.setDegreeName("Updated Bachelor");
        updatedDegree.setProgramName("Updated Description");

        // Mock service calls
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(existingDegree);
        when(academicDegreeService.updateAcademicDegree(eq(1L), any(AcademicDegreeDto.class))).thenReturn(updatedDegree);

        // Perform PATCH request
        mockMvc.perform(patch("/academic-degree/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(academicDegreeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Academic Degree is updated successfully"))
                .andExpect(jsonPath("$.data.degreeId").value(1))
                .andExpect(jsonPath("$.data.degree_name").value("Updated Bachelor"));

        // Verify service methods were called
        verify(academicDegreeService).getAcademicDegreeById(1L);
        verify(academicDegreeService).updateAcademicDegree(eq(1L), any(AcademicDegreeDto.class));
    }

    @Test
    @DisplayName("test_deleteAcademicDegree_success")
    void testDeleteAcademicDegree() throws Exception {
        // Create sample academic degree
        AcademicDegree academicDegree = new AcademicDegree();
        academicDegree.setDegreeId(1L);
        academicDegree.setDegreeName("Bachelor of Science");

        // Mock service calls
        when(academicDegreeService.getAcademicDegreeById(1L)).thenReturn(academicDegree);
        when(academicDegreeService.deleteAcademicDegreeById(1L)).thenReturn(academicDegree);

        // Perform DELETE request
        mockMvc.perform(delete("/academic-degree/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Academic degree is archived successfully"))
                .andExpect(jsonPath("$.data.degreeId").value(1));

        // Verify service methods were called
        verify(academicDegreeService).getAcademicDegreeById(1L);
        verify(academicDegreeService).deleteAcademicDegreeById(1L);
    }

    @Test
    @DisplayName("test_deleteAcademicDegree_notFound")
    void testDeleteAcademicDegreeNotFound() throws Exception {
        // Mock not found scenario
        when(academicDegreeService.getAcademicDegreeById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/academic-degree/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called but delete was not
        verify(academicDegreeService).getAcademicDegreeById(999L);
        verify(academicDegreeService, never()).deleteAcademicDegreeById(999L);
    }
}