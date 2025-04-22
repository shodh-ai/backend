package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.GenderController;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Service.DoubtService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.GenderService;
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
@WebMvcTest(GenderController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class GenderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenderService genderService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllGenders")
    void testGetAllGenders() throws Exception {
        // Create sample content types
        List<Gender> genders = new ArrayList<>();
        Gender type1 = new Gender();
        type1.setGenderId(1L);
        type1.setGenderName("Article");

        Gender type2 = new Gender();
        type2.setGenderId(2L);
        type2.setGenderName("Video");

        genders.add(type1);
        genders.add(type2);

        // Mock service call
        when(genderService.getAllGender()).thenReturn(genders);

        // Perform GET request
        mockMvc.perform(get("/gender/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Gender Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(genderService).getAllGender();
    }

    @Test
    @DisplayName("testGetAllGendersEmptyList")
    void testGetAllGendersEmptyList() throws Exception {
        // Mock empty list response
        when(genderService.getAllGender()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/gender/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(genderService).getAllGender();
    }

    @Test
    @DisplayName("testGetGenderById")
    void testGetGenderById() throws Exception {
        // Create sample content type
        Gender gender = new Gender();
        gender.setGenderId(1L);
        gender.setGenderName("Article");

        // Mock service call
        when(genderService.getGenderById(1L)).thenReturn(gender);

        // Perform GET request
        mockMvc.perform(get("/gender/get-gender-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Gender Retrieved Successfully"))
                .andExpect(jsonPath("$.data.gender_id").value(1L));


        // Verify service method was called
        verify(genderService).getGenderById(1L);
    }

    @Test
    @DisplayName("testGetGenderByIdNotFound")
    void testGetGenderByIdNotFound() throws Exception {
        // Mock not found scenario
        when(genderService.getGenderById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/gender/get-gender-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(genderService).getGenderById(999L);
    }
}