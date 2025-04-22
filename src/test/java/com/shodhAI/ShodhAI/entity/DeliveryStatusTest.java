package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeliveryStatusTest {
    private Long deliveryStatusId;
    private String deliveryStatusName;
    private Character archived;
    private String deliveryStatusDescription;

    @BeforeEach
    void setUp() {
        deliveryStatusId = 1L;
        deliveryStatusName = "deliveryStatusName";
        deliveryStatusDescription = "deliveryStatusDescription";
        archived = 'N';
    }

    @Test
    @DisplayName("testDeliveryStatusConstructor")
    void testDeliveryStatusConstructor(){
        DeliveryStatus deliveryStatusByConstructor =  DeliveryStatus.builder().id(deliveryStatusId).code(deliveryStatusName).archived(archived).description(deliveryStatusDescription).build();

        assertEquals(deliveryStatusId, deliveryStatusByConstructor.getId());
        assertEquals(deliveryStatusName, deliveryStatusByConstructor.getCode());
        assertEquals(deliveryStatusDescription, deliveryStatusByConstructor.getDescription());
        assertEquals(archived, deliveryStatusByConstructor.getArchived());
    }

    @Test
    @DisplayName("testDeliveryStatusSettersAndGetters")
    void testDeliveryStatusSettersAndGetters(){
        DeliveryStatus deliveryStatus = new DeliveryStatus();
        deliveryStatus.setId(deliveryStatusId);
        deliveryStatus.setCode(deliveryStatusName);
        deliveryStatus.setDescription(deliveryStatusDescription);
        deliveryStatus.setArchived(archived);
        assertEquals(deliveryStatusId, deliveryStatus.getId());
        assertEquals(deliveryStatusName, deliveryStatus.getCode());
        assertEquals(archived, deliveryStatus.getArchived());
    }
}


