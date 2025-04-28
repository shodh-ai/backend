package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Controller.SemesterController;
import com.shodhAI.ShodhAI.Dto.SemesterDto;
import com.shodhAI.ShodhAI.Entity.Semester;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import com.shodhAI.ShodhAI.Service.SemesterService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(SemesterController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes ={TestSecurityConfig.class, TestJwtConfig.class} )
public class SemesterControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoleService roleService;
    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private SemesterService semesterService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @Test
    @DisplayName("testCreateSemester")
    void testCreateSemester() throws Exception {
        // Create a sample SemesterDto
        SemesterDto semesterDto = new SemesterDto();
        semesterDto.setSemesterName("Fall 2025");

        Semester semester = new Semester();
        semester.setSemesterId(1L);
        semester.setSemesterName("Fall 2025");

        // Mock service calls
        doNothing().when(semesterService).validateSemester(any(SemesterDto.class));
        when(semesterService.saveSemester(any(SemesterDto.class))).thenReturn(semester);

        // Perform POST request
        mockMvc.perform(post("/semester/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(semesterDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Semester is created successfully"))
                .andExpect(jsonPath("$.data.semester_id").value(1L))
                .andExpect(jsonPath("$.data.semester_name").value("Fall 2025"));

        // Verify service methods were called
        verify(semesterService).validateSemester(any(SemesterDto.class));
        verify(semesterService).saveSemester(any(SemesterDto.class));
    }

    @Test
    @DisplayName("testCreateSemesterValidationError")
    void testCreateSemesterValidationError() throws Exception {
        // Create an invalid SemesterDto
        SemesterDto semesterDto = new SemesterDto();
        semesterDto.setSemesterName(""); // Invalid empty name

        // Mock validation exception
        doThrow(new IllegalArgumentException("Semester name cannot be empty"))
                .when(semesterService).validateSemester(any(SemesterDto.class));

        // Perform POST request
        mockMvc.perform(post("/semester/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(semesterDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Semester name cannot be empty"));

        // Verify service methods were called correctly
        verify(semesterService).validateSemester(any(SemesterDto.class));
        verify(semesterService, never()).saveSemester(any(SemesterDto.class));
    }

    @Test
    @DisplayName("testRetrieveAllSemesters")
    void testRetrieveAllSemesters() throws Exception {
        // Create sample semesters
        List<Semester> semesters = new ArrayList<>();
        Semester semester1 = new Semester();
        semester1.setSemesterId(1L);
        semester1.setSemesterName("Fall 2025");

        Semester semester2 = new Semester();
        semester2.setSemesterId(2L);
        semester2.setSemesterName("Spring 2026");

        semesters.add(semester1);
        semesters.add(semester2);

        // Mock service call
        when(semesterService.getAllSemesters()).thenReturn(semesters);

        // Perform GET request
        mockMvc.perform(get("/semester/get-all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Semester list is Retrieved Successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].semester_id").value(1L))
                .andExpect(jsonPath("$.data[1].semester_id").value(2L));

        // Verify service method was called
        verify(semesterService).getAllSemesters();
    }

    @Test
    @DisplayName("testRetrieveAllSemestersEmpty")
    void testRetrieveAllSemestersEmpty() throws Exception {
        // Mock empty list
        when(semesterService.getAllSemesters()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/semester/get-all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Semester list is empty in Database"))
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service method was called
        verify(semesterService).getAllSemesters();
    }

    @Test
    @DisplayName("testRetrieveSemesterById")
    void testRetrieveSemesterById() throws Exception {
        // Create sample semester
        Semester semester = new Semester();
        semester.setSemesterId(1L);
        semester.setSemesterName("Fall 2025");

        // Mock service call
        when(semesterService.getSemesterById(1L)).thenReturn(semester);

        // Perform GET request
        mockMvc.perform(get("/semester/get-semester-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Semester is Retrieved Successfully"))
                .andExpect(jsonPath("$.data.semester_id").value(1))
                .andExpect(jsonPath("$.data.semester_name").value("Fall 2025"));

        // Verify service method was called
        verify(semesterService).getSemesterById(1L);
    }

    @Test
    @DisplayName("testRetrieveSemesterByIdNotFound")
    void testRetrieveSemesterByIdNotFound() throws Exception {
        // Mock not found scenario
        when(semesterService.getSemesterById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/semester/get-semester-by-id/999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Semester with id 999 does not exist"));

        // Verify service method was called
        verify(semesterService).getSemesterById(999L);
    }

    @Test
    @DisplayName("testUpdateSemester")
    void testUpdateSemester() throws Exception {
        // Create sample DTO and entity
        SemesterDto semesterDto = new SemesterDto();
        semesterDto.setSemesterName("Updated Fall 2025");

        Semester updatedSemester = new Semester();
        updatedSemester.setSemesterId(1L);
        updatedSemester.setSemesterName("Updated Fall 2025");

        // Mock service calls
        when(semesterService.getSemesterById(1L)).thenReturn(updatedSemester);
        when(semesterService.updateSemester(eq(1L), any(SemesterDto.class))).thenReturn(updatedSemester);

        // Perform PATCH request
        mockMvc.perform(patch("/semester/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(semesterDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Semester is updated successfully"))
                .andExpect(jsonPath("$.data.semester_id").value(1))
                .andExpect(jsonPath("$.data.semester_name").value("Updated Fall 2025"));

        // Verify service methods were called
        verify(semesterService).getSemesterById(1L);
        verify(semesterService).updateSemester(eq(1L), any(SemesterDto.class));
    }

    @Test
    @DisplayName("testUpdateSemesterNotFound")
    void testUpdateSemesterNotFound() throws Exception {
        // Create sample DTO
        SemesterDto semesterDto = new SemesterDto();
        semesterDto.setSemesterName("Updated Semester");

        // Mock service call for non-existent semester
        when(semesterService.getSemesterById(999L)).thenReturn(null);

        // Perform PATCH request
        mockMvc.perform(patch("/semester/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(semesterDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Semester with id 999 does not exist"));

        // Verify service method was called but update was not
        verify(semesterService).getSemesterById(999L);
        verify(semesterService, never()).updateSemester(eq(999L), any(SemesterDto.class));
    }

    @Test
    @DisplayName("testGetFilterSemester")
    void testGetFilterSemester() throws Exception {
        // Create sample semesters
        List<Semester> semesters = new ArrayList<>();
        Semester semester1 = new Semester();
        semester1.setSemesterId(1L);
        semester1.setSemesterName("Fall 2025");

        Semester semester2 = new Semester();
        semester2.setSemesterId(2L);
        semester2.setSemesterName("Spring 2026");

        semesters.add(semester1);
        semesters.add(semester2);

        // Create response map
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("semesterList", semesters);
        responseMap.put("totalItems", 2);
        responseMap.put("totalPages", 1);
        responseMap.put("currentPage", 0);

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(semesterService.semesterFilter(eq(1L), eq(1L), eq(1L), eq(1L))).thenReturn(semesters);

        // Perform GET request
        mockMvc.perform(get("/semester/get-filter-semester")
                        .param("semesterId", "1")
                        .param("academicDegreeId", "1")
                        .param("offset", "0")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Semesters Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.semesterList.length()").value(2));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(semesterService).semesterFilter(eq(1L), eq(1L), eq(1L), eq(1L));
    }

    @Test
    @DisplayName("testGetFilterSemesterEmptyResult")
    void testGetFilterSemesterEmptyResult() throws Exception {
        // Create empty list
        List<Semester> emptySemesters = new ArrayList<>();

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(semesterService.semesterFilter(any(), eq(1L), eq(1L), any())).thenReturn(emptySemesters);

        // Perform GET request
        mockMvc.perform(get("/semester/get-filter-semester")
                        .param("offset", "0")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No semesters found with the given criteria"))
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(semesterService).semesterFilter(any(), eq(1L), eq(1L), any());
    }

    @Test
    @DisplayName("testGetFilterSemesterInvalidPagination")
    void testGetFilterSemesterInvalidPagination() throws Exception {
        // Perform GET request with negative offset
        mockMvc.perform(get("/semester/get-filter-semester")
                        .param("offset", "-1")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Offset for pagination cannot be a negative number"));

        // Verify no service methods were called
        verify(semesterService, never()).semesterFilter(any(), any(), any(), any());
    }

    @Test
    @DisplayName("testGetFilterSemesterZeroLimit")
    void testGetFilterSemesterZeroLimit() throws Exception {
        // Perform GET request with zero limit
        mockMvc.perform(get("/semester/get-filter-semester")
                        .param("offset", "0")
                        .param("limit", "0")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Limit for pagination cannot be a negative number or 0"));

        // Verify no service methods were called
        verify(semesterService, never()).semesterFilter(any(), any(), any(), any());
    }

    @Test
    @DisplayName("testGetFilterSemesterPageOutOfRange")
    void testGetFilterSemesterPageOutOfRange() throws Exception {
        // Create sample semesters list with only 5 items
        List<Semester> semesters = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Semester semester = new Semester();
            semester.setSemesterId((long) i);
            semester.setSemesterName("Semester " + i);
            semesters.add(semester);
        }

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(semesterService.semesterFilter(any(), eq(1L), eq(1L), any())).thenReturn(semesters);

        // Perform GET request with offset beyond available pages
        mockMvc.perform(get("/semester/get-filter-semester")
                        .param("offset", "2") // Page 2 (assuming 5 items per page would be page 0)
                        .param("limit", "5")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("No more semesters available"));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(semesterService).semesterFilter(any(), eq(1L), eq(1L), any());
    }

    @Test
    @DisplayName("testGetFilterSemesterNoMoreSemestersAvailable")
    void testGetFilterSemesterNoMoreSemestersAvailable() throws Exception {
        // Create sample semesters list with only 5 items
        List<Semester> semesters = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Semester semester = new Semester();
            semester.setSemesterId((long) i);
            semester.setSemesterName("Semester " + i);
            semesters.add(semester);
        }

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call to return semesters, but then we'll check offset logic in controller
        when(semesterService.semesterFilter(any(), eq(1L), eq(1L), any())).thenReturn(semesters);

        // Perform GET request with offset beyond total pages
        mockMvc.perform(get("/semester/get-filter-semester")
                        .param("offset", "5") // Way beyond available pages with 5 items total
                        .param("limit", "1")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("No more semesters available"));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(semesterService).semesterFilter(any(), eq(1L), eq(1L), any());
    }
}