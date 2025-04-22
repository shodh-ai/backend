package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Service.DeliveryStatusService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryStatusServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<DeliveryStatus> deliveryStatusTypedQuery;

    @InjectMocks
    private DeliveryStatusService deliveryStatusService;

    private DeliveryStatus validDeliveryStatus;
    private List<DeliveryStatus> deliveryStatusList;

    @BeforeEach
    void setUp() {
        // Setup valid doubt
        validDeliveryStatus = new DeliveryStatus();
        validDeliveryStatus.setId(1L);
        validDeliveryStatus.setCode("Article");
        validDeliveryStatus.setArchived('N');

        // Setup doubt  list
        DeliveryStatus deliveryStatus2 = new DeliveryStatus();
        deliveryStatus2.setId(2L);
        deliveryStatus2.setCode("Video");
        deliveryStatus2.setArchived('N');

        deliveryStatusList = Arrays.asList(validDeliveryStatus, deliveryStatus2);
    }

    @Test
    @DisplayName("Should get doubt  by ID successfully")
    void testGetDeliveryStatusById() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_DELIVERY_STATUS_BY_ID), eq(DeliveryStatus.class)))
                .thenReturn(deliveryStatusTypedQuery);
        when(deliveryStatusTypedQuery.setParameter(eq("deliveryStatusId"), eq(1L))).thenReturn(deliveryStatusTypedQuery);
        when(deliveryStatusTypedQuery.getResultList()).thenReturn(Collections.singletonList(validDeliveryStatus));

        // When
        DeliveryStatus result = deliveryStatusService.getDeliveryStatusById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Article", result.getCode());

        verify(entityManager).createQuery(eq(Constant.GET_DELIVERY_STATUS_BY_ID), eq(DeliveryStatus.class));
        verify(deliveryStatusTypedQuery).setParameter(eq("deliveryStatusId"), eq(1L));
        verify(deliveryStatusTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should throw exception when doubt  not found by ID")
    void testGetDeliveryStatusByIdNotFound() {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_DELIVERY_STATUS_BY_ID), eq(DeliveryStatus.class)))
                .thenReturn(deliveryStatusTypedQuery);
        when(deliveryStatusTypedQuery.setParameter(eq("deliveryStatusId"), eq(99L))).thenReturn(deliveryStatusTypedQuery);
        when(deliveryStatusTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // When & Then
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class,
                () -> deliveryStatusService.getDeliveryStatusById(99L));

        verify(exceptionHandlingService).handleException(any(IndexOutOfBoundsException.class));
    }
}
