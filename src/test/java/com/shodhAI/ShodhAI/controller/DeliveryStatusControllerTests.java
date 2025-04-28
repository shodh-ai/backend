package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.DeliveryStatusController;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Service.DoubtService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.DeliveryStatusService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(DeliveryStatusController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class DeliveryStatusControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private DeliveryStatusService deliveryStatusService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetDeliveryStatusById")
    void testGetDeliveryStatusById() throws Exception {
        // Create sample content type
        DeliveryStatus deliveryStatus = new DeliveryStatus();
        deliveryStatus.setId(1L);
        deliveryStatus.setCode("Article");

        // Mock service call
        when(deliveryStatusService.getDeliveryStatusById(1L)).thenReturn(deliveryStatus);

        // Perform GET request
        mockMvc.perform(get("/delivery-status/get-delivery-status-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Delivery status Retrieved Successfully"))
                .andExpect(jsonPath("$.data.delivery_status_id").value(1L));


        // Verify service method was called
        verify(deliveryStatusService).getDeliveryStatusById(1L);
    }

    @Test
    @DisplayName("testGetDeliveryStatusByIdNotFound")
    void testGetDeliveryStatusByIdNotFound() throws Exception {
        // Mock not found scenario
        when(deliveryStatusService.getDeliveryStatusById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/delivery-status/get-delivery-status-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(deliveryStatusService).getDeliveryStatusById(999L);
    }
}