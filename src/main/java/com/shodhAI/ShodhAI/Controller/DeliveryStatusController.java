package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Service.DeliveryStatusService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery-status")
public class DeliveryStatusController
{
    @Autowired
    EntityManager entityManager;

    @Autowired
    DeliveryStatusService deliveryStatusService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @GetMapping("/get-delivery-status-by-id/{deliveryStatusIdString}")
    public ResponseEntity<?> retrieveDeliveryStatusById(HttpServletRequest request, @PathVariable String deliveryStatusIdString) {
        try {

            Long genderId = Long.parseLong(deliveryStatusIdString);
            DeliveryStatus deliveryStatus = deliveryStatusService.getDeliveryStatusById(genderId);
            if (deliveryStatus == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Delivery status Retrieved Successfully", deliveryStatus, HttpStatus.OK);

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse("Illegal Argument exception caught: " + illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            return ResponseService.generateErrorResponse("Index out of bound exception Caught: " + indexOutOfBoundsException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

