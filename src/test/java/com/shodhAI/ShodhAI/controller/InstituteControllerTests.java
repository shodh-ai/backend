package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Component.TokenBlacklist;
import com.shodhAI.ShodhAI.Controller.InstituteController;
import com.shodhAI.ShodhAI.Dto.InstituteDto;
import com.shodhAI.ShodhAI.Entity.Institute;
import com.shodhAI.ShodhAI.Service.AcademicDegreeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.InstituteService;
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
@WebMvcTest(InstituteController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class InstituteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private AcademicDegreeService academicDegreeService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private InstituteService instituteService;


    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @Test
    @DisplayName("test_addInstitute_success")
    void testAddInstitute() throws Exception {
        // Create a sample InstituteDto
        InstituteDto instituteDto = new InstituteDto();
        instituteDto.setInstitutionName("TestInstitute");

        Institute institute = new Institute();
        institute.setInstituteId(1L);
        institute.setInstitutionName("TestInstitute");

        // Mock service calls
        doNothing().when(instituteService).validateInstitute(any(InstituteDto.class));
        when(instituteService.saveInstitute(any(InstituteDto.class))).thenReturn(institute);

        // Perform POST request
        mockMvc.perform(post("/institute/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(instituteDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Institute Created Successfully"))
                .andExpect(jsonPath("$.data.instituteId").value(1L))
                .andExpect(jsonPath("$.data.institution_name").value("TestInstitute"));

        // Verify service methods were called
        verify(instituteService).validateInstitute(any(InstituteDto.class));
        verify(instituteService).saveInstitute(any(InstituteDto.class));
    }

    @Test
    @DisplayName("test_addInstitute_validationError")
    void testAddInstituteValidationError() throws Exception {
        // Create a sample InstituteDto with invalid data
        InstituteDto instituteDto = new InstituteDto();
        instituteDto.setInstitutionName(""); // Invalid empty name

        // Mock validation exception
        doThrow(new IllegalArgumentException("Name cannot be empty"))
                .when(instituteService).validateInstitute(any(InstituteDto.class));

        // Perform POST request
        mockMvc.perform(post("/institute/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(instituteDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Illegal Exception Caught: Name cannot be empty"));

        // Verify service methods were called correctly
        verify(instituteService).validateInstitute(any(InstituteDto.class));
        verify(instituteService, never()).saveInstitute(any(InstituteDto.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("test_filterInstitute_success")
    void testFilterInstitute() throws Exception {
        // Create sample institutes
        List<Institute> institutes = new ArrayList<>();
        Institute institute1 = new Institute();
        institute1.setInstituteId(1L);
        institute1.setInstitutionName("Institute One");

        Institute institute2 = new Institute();
        institute2.setInstituteId(2L);
        institute2.setInstitutionName("Institute Two");

        institutes.add(institute1);
        institutes.add(institute2);

        // Mock service call
        when(instituteService.filterInstitute(null, null)).thenReturn(institutes);

        // Perform GET request
        mockMvc.perform(get("/institute/filter-institute")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Institute Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(instituteService).filterInstitute(null, null);
    }

    @Test
    @DisplayName("test_filterInstitute_emptyList")
    void testFilterInstituteEmptyList() throws Exception {
        // Mock empty list response
        when(instituteService.filterInstitute(null, null)).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/institute/filter-institute")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(instituteService).filterInstitute(null, null);
    }

    @Test
    @DisplayName("test_getInstituteById_success")
    void testGetInstituteById() throws Exception {
        // Create sample institute
        Institute institute = new Institute();
        institute.setInstituteId(1L);
        institute.setInstitutionName("Test Institute");

        // Mock service call
        when(instituteService.getInstituteById(1L)).thenReturn(institute);

        // Perform GET request
        mockMvc.perform(get("/institute/get-institute-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Institute Retrieved Successfully"))
                .andExpect(jsonPath("$.data.instituteId").value(1))
                .andExpect(jsonPath("$.data.institution_name").value("Test Institute"));

        // Verify service method was called
        verify(instituteService).getInstituteById(1L);
    }

    @Test
    @DisplayName("test_getInstituteById_notFound")
    void testGetInstituteByIdNotFound() throws Exception {
        // Mock not found scenario
        when(instituteService.getInstituteById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/institute/get-institute-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(instituteService).getInstituteById(999L);
    }

    @Test
    @DisplayName("test_deleteInstituteById_success")
    void testDeleteInstituteById() throws Exception {
        // Create sample institute
        Institute institute = new Institute();
        institute.setInstituteId(1L);
        institute.setInstitutionName("Test Institute");

        // Mock service calls
        when(instituteService.getInstituteById(1L)).thenReturn(institute);
        when(instituteService.removeInstituteById(any(Institute.class))).thenReturn(institute);

        // Perform DELETE request
        mockMvc.perform(delete("/institute/delete-institute-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Institute Archived Successfully"))
                .andExpect(jsonPath("$.data.instituteId").value(1));

        // Verify service methods were called
        verify(instituteService).getInstituteById(1L);
        verify(instituteService).removeInstituteById(any(Institute.class));
    }

    @Test
    @DisplayName("test_deleteInstituteById_notFound")
    void testDeleteInstituteByIdNotFound() throws Exception {
        // Mock not found scenario
        when(instituteService.getInstituteById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/institute/delete-institute-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called but delete was not
        verify(instituteService).getInstituteById(999L);
        verify(instituteService, never()).removeInstituteById(any(Institute.class));
    }

    @Test
    @DisplayName("test_updateInstituteById_success")
    void testUpdateInstituteById() throws Exception {
        // Create sample DTO and entity
        InstituteDto instituteDto = new InstituteDto();
        instituteDto.setInstitutionName("Updated Institute");

        Institute existingInstitute = new Institute();
        existingInstitute.setInstituteId(1L);
        existingInstitute.setInstitutionName("Test Institute");

        Institute updatedInstitute = new Institute();
        updatedInstitute.setInstituteId(1L);
        updatedInstitute.setInstitutionName("Updated Institute");

        // Mock service calls
        when(instituteService.getInstituteById(1L)).thenReturn(existingInstitute);
        doNothing().when(instituteService).validateUpdateInstitute(any(InstituteDto.class));
        when(instituteService.updateInstituteById(any(Institute.class), any(InstituteDto.class))).thenReturn(updatedInstitute);

        // Perform PUT request
        mockMvc.perform(put("/institute/update-institute-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(instituteDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Institute Archived Successfully"))
                .andExpect(jsonPath("$.data.instituteId").value(1))
                .andExpect(jsonPath("$.data.institution_name").value("Updated Institute"));

        // Verify service methods were called
        verify(instituteService).getInstituteById(1L);
        verify(instituteService).validateUpdateInstitute(any(InstituteDto.class));
        verify(instituteService).updateInstituteById(any(Institute.class), any(InstituteDto.class));
    }

}