package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Service.NotificationTypeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification-type")
public class NotificationTypeController
{
    @Autowired
    EntityManager entityManager;

    @Autowired
    NotificationTypeService notificationTypeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    @GetMapping("/get-notification-type-by-id/{notificationTypeIdString}")
    public ResponseEntity<?> retrieveNotificationTypeById(HttpServletRequest request, @PathVariable String notificationTypeIdString) {
        try {

            Long genderId = Long.parseLong(notificationTypeIdString);
            NotificationType notificationType = notificationTypeService.getNotificationTypeById(genderId);
            if (notificationType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("Notification Type Retrieved Successfully", notificationType, HttpStatus.OK);

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
