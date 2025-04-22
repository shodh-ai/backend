package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.RoleController;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Service.DoubtService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
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
@WebMvcTest(RoleController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class RoleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllRoles")
    void testGetAllRoles() throws Exception {
        // Create sample content types
        List<Role> roles = new ArrayList<>();
        Role type1 = new Role();
        type1.setRoleId(1L);
        type1.setRoleName("Article");

        Role type2 = new Role();
        type2.setRoleId(2L);
        type2.setRoleName("Video");

        roles.add(type1);
        roles.add(type2);

        // Mock service call
        when(roleService.getAllRole()).thenReturn(roles);

        // Perform GET request
        mockMvc.perform(get("/role/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Role Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(roleService).getAllRole();
    }

    @Test
    @DisplayName("testGetAllRolesEmptyList")
    void testGetAllRolesEmptyList() throws Exception {
        // Mock empty list response
        when(roleService.getAllRole()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/role/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(roleService).getAllRole();
    }

    @Test
    @DisplayName("testGetRoleById")
    void testGetRoleById() throws Exception {
        // Create sample content type
        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName("Article");

        // Mock service call
        when(roleService.getRoleById(1L)).thenReturn(role);

        // Perform GET request
        mockMvc.perform(get("/role/get-role-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Role Retrieved Successfully"))
                .andExpect(jsonPath("$.data.role_id").value(1L));


        // Verify service method was called
        verify(roleService).getRoleById(1L);
    }

    @Test
    @DisplayName("testGetRoleByIdNotFound")
    void testGetRoleByIdNotFound() throws Exception {
        // Mock not found scenario
        when(roleService.getRoleById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/role/get-role-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(roleService).getRoleById(999L);
    }
}