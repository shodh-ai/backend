package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Component.JwtUtil;
import com.shodhAI.ShodhAI.Controller.ModuleController;
import com.shodhAI.ShodhAI.Dto.ModuleDto;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ModuleService;
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
@WebMvcTest(ModuleController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private ModuleService moduleService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @Test
    @DisplayName("testAddModule")
    void testAddModule() throws Exception {
        // Create a sample ModuleDto
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setModuleTitle("Introduction to Java");
        moduleDto.setModuleDescription("Basic Java programming concepts");

        Module module = new Module();
        module.setModuleId(1L);
        module.setModuleTitle("Introduction to Java");
        module.setModuleDescription("Basic Java programming concepts");

        // Mock service calls
        doNothing().when(moduleService).validateModule(any(ModuleDto.class));
        when(moduleService.saveModule(any(ModuleDto.class))).thenReturn(module);

        // Perform POST request
        mockMvc.perform(post("/module/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(moduleDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Module Created Successfully"))
                .andExpect(jsonPath("$.data.moduleId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Introduction to Java"));

        // Verify service methods were called
        verify(moduleService).validateModule(any(ModuleDto.class));
        verify(moduleService).saveModule(any(ModuleDto.class));
    }

    @Test
    @DisplayName("testAddModuleValidationError")
    void testAddModuleValidationError() throws Exception {
        // Create an invalid ModuleDto
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setModuleTitle(""); // Invalid empty name

        // Mock validation exception
        doThrow(new IllegalArgumentException("Module name cannot be empty"))
                .when(moduleService).validateModule(any(ModuleDto.class));

        // Perform POST request
        mockMvc.perform(post("/module/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(moduleDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Illegal Exception Caught: Module name cannot be empty"));

        // Verify service methods were called correctly
        verify(moduleService).validateModule(any(ModuleDto.class));
        verify(moduleService, never()).saveModule(any(ModuleDto.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testGetModuleById")
    void testGetModuleById() throws Exception {
        // Create sample module
        Module module = new Module();
        module.setModuleId(1L);
        module.setModuleTitle("Introduction to Java");
        module.setModuleDescription("Basic Java programming concepts");

        // Mock service call
        when(moduleService.getModuleById(1L)).thenReturn(module);

        // Perform GET request
        mockMvc.perform(get("/module/get-module-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Module Retrieved Successfully"))
                .andExpect(jsonPath("$.data.moduleId").value(1))
                .andExpect(jsonPath("$.data.title").value("Introduction to Java"));

        // Verify service method was called
        verify(moduleService).getModuleById(1L);
    }

    @Test
    @DisplayName("testGetModuleByIdNotFound")
    void testGetModuleByIdNotFound() throws Exception {
        // Mock not found scenario
        when(moduleService.getModuleById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/module/get-module-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(moduleService).getModuleById(999L);
    }

    @Test
    @DisplayName("testUpdateModule")
    void testUpdateModule() throws Exception {
        // Create sample DTO and entity
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setModuleTitle("Updated Java Module");
        moduleDto.setModuleDescription("Updated Java concepts");

        Module updatedModule = new Module();
        updatedModule.setModuleId(1L);
        updatedModule.setModuleTitle("Updated Java Module");
        updatedModule.setModuleDescription("Updated Java concepts");

        // Mock service calls
        when(moduleService.updateModule(eq(1L), any(ModuleDto.class))).thenReturn(updatedModule);

        // Perform PATCH request
        mockMvc.perform(patch("/module/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(moduleDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Module updated Successfully"))
                .andExpect(jsonPath("$.data.moduleId").value(1))
                .andExpect(jsonPath("$.data.title").value("Updated Java Module"));

        // Verify service methods were called
        verify(moduleService).updateModule(eq(1L), any(ModuleDto.class));
    }

    @Test
    @DisplayName("testUpdateModuleNotFound")
    void testUpdateModuleNotFound() throws Exception {
        // Create sample DTO
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setModuleTitle("Updated Java Module");

        // Mock exception for non-existent module
        when(moduleService.updateModule(eq(999L), any(ModuleDto.class)))
                .thenThrow(new IllegalArgumentException("Module with id 999 not found"));

        // Perform PATCH request
        mockMvc.perform(patch("/module/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(moduleDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Illegal Exception Caught: Module with id 999 not found"));

        // Verify service method was called
        verify(moduleService).updateModule(eq(999L), any(ModuleDto.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testDeleteModule")
    void testDeleteModule() throws Exception {
        // Create sample module
        Module module = new Module();
        module.setModuleId(1L);
        module.setModuleTitle("Introduction to Java");
        module.setModuleDescription("Basic Java programming concepts");

        // Mock service calls
        when(moduleService.getModuleById(1L)).thenReturn(module);
        when(moduleService.deleteModuleById(1L)).thenReturn(module);

        // Perform DELETE request
        mockMvc.perform(delete("/module/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Module is successfully deleted"))
                .andExpect(jsonPath("$.data.moduleId").value(1));

        // Verify service methods were called
        verify(moduleService).getModuleById(1L);
        verify(moduleService).deleteModuleById(1L);
    }

    @Test
    @DisplayName("testDeleteModuleNotFound")
    void testDeleteModuleNotFound() throws Exception {
        // Mock not found scenario
        when(moduleService.getModuleById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/module/delete/999"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Module with id 999 not found"));

        // Verify service method was called but delete was not
        verify(moduleService).getModuleById(999L);
        verify(moduleService, never()).deleteModuleById(999L);
    }

    @Test
    @DisplayName("testGetFilterModule")
    void testGetFilterModule() throws Exception {
        // Create sample modules
        List<Module> modules = new ArrayList<>();
        Module module1 = new Module();
        module1.setModuleId(1L);
        module1.setModuleTitle("Introduction to Java");
        module1.setModuleDescription("Basic Java concepts");

        Module module2 = new Module();
        module2.setModuleId(2L);
        module2.setModuleTitle("Advanced Java");
        module2.setModuleDescription("Advanced Java concepts");

        modules.add(module1);
        modules.add(module2);

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(moduleService.moduleFilter(eq(1L), eq(1L), eq(1L), eq(1L), eq(1L))).thenReturn(modules);

        // Perform GET request
        mockMvc.perform(get("/module/get-filter-module")
                        .param("moduleId", "1")
                        .param("courseId", "1")
                        .param("academicDegreeId", "1")
                        .param("offset", "0")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Modules Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0))
                .andExpect(jsonPath("$.data.modules.length()").value(2));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(moduleService).moduleFilter(eq(1L), eq(1L), eq(1L), eq(1L), eq(1L));
    }

    @Test
    @DisplayName("testGetFilterModuleEmptyResult")
    void testGetFilterModuleEmptyResult() throws Exception {
        // Create empty list
        List<Module> emptyModules = new ArrayList<>();

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(moduleService.moduleFilter(any(), eq(1L), eq(1L), any(), any())).thenReturn(emptyModules);

        // Perform GET request
        mockMvc.perform(get("/module/get-filter-module")
                        .param("offset", "0")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No modules found with the given criteria"))
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(moduleService).moduleFilter(any(), eq(1L), eq(1L), any(), any());
    }

    @Test
    @DisplayName("testGetFilterModuleInvalidPagination")
    void testGetFilterModuleInvalidPagination() throws Exception {
        // Perform GET request with negative offset
        mockMvc.perform(get("/module/get-filter-module")
                        .param("offset", "-1")
                        .param("limit", "10")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Offset for pagination cannot be a negative number"));

        // Verify no service methods were called
        verify(moduleService, never()).moduleFilter(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("testGetFilterModuleZeroLimit")
    void testGetFilterModuleZeroLimit() throws Exception {
        // Perform GET request with zero limit
        mockMvc.perform(get("/module/get-filter-module")
                        .param("offset", "0")
                        .param("limit", "0")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Limit for pagination cannot be a negative number or 0"));

        // Verify no service methods were called
        verify(moduleService, never()).moduleFilter(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("testGetFilterModulePageOutOfRange")
    void testGetFilterModulePageOutOfRange() throws Exception {
        // Create sample modules list with only 5 items
        List<Module> modules = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Module module = new Module();
            module.setModuleId((long) i);
            module.setModuleTitle("Module " + i);
            modules.add(module);
        }

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call
        when(moduleService.moduleFilter(any(), eq(1L), eq(1L), any(), any())).thenReturn(modules);

        // Perform GET request with offset beyond available pages
        mockMvc.perform(get("/module/get-filter-module")
                        .param("offset", "2") // Page 2 (assuming 5 items per page would be page 0)
                        .param("limit", "5")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("No more modules available"));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(moduleService).moduleFilter(any(), eq(1L), eq(1L), any(), any());
    }

    @Test
    @DisplayName("testGetFilterModuleNoMoreModulesAvailable")
    void testGetFilterModuleNoMoreModulesAvailable() throws Exception {
        // Create sample modules list with only 5 items
        List<Module> modules = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Module module = new Module();
            module.setModuleId((long) i);
            module.setModuleTitle("Module " + i);
            modules.add(module);
        }

        // Mock JWT token extraction
        when(jwtUtil.extractRoleId("mock-token")).thenReturn(1L);
        when(jwtUtil.extractId("mock-token")).thenReturn(1L);

        // Mock service call to return modules, but then we'll check offset logic in controller
        when(moduleService.moduleFilter(any(), eq(1L), eq(1L), any(), any())).thenReturn(modules);

        // Perform GET request with offset beyond total pages
        mockMvc.perform(get("/module/get-filter-module")
                        .param("offset", "5") // Way beyond available pages with 5 items total
                        .param("limit", "1")
                        .header("Authorization", "Bearer mock-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("No more modules available"));

        // Verify service methods were called
        verify(jwtUtil).extractRoleId("mock-token");
        verify(jwtUtil).extractId("mock-token");
        verify(moduleService).moduleFilter(any(), eq(1L), eq(1L), any(), any());
    }
}