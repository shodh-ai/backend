package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.NotificationType;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/add")
    public ResponseEntity<?> addNotificationType(@RequestBody NotificationType notificationType)
    {
        try
        {
            NotificationType notificationTypeToSave=notificationTypeService.addNotificationType(notificationType);
            return ResponseService.generateSuccessResponse("Notification type is successfully added",notificationTypeToSave, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{notificationTypeIdString}")
    public ResponseEntity<?> deleteNotificationTpe (@PathVariable String notificationTypeIdString)
    {
        try
        {
            Long notificationTypeId = Long.parseLong(notificationTypeIdString);
            NotificationType notificationType = notificationTypeService.getNotificationTypeById(notificationTypeId);
            if (notificationType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            NotificationType deletedNotificationType =notificationTypeService.deleteNotificationTypeById(notificationTypeId);
            return ResponseService.generateSuccessResponse("Notification type is archived successfully",deletedNotificationType ,HttpStatus.OK);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            exceptionHandlingService.handleException(illegalArgumentException);
            return ResponseService.generateErrorResponse(illegalArgumentException.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{notificationTypeIdString}")
    public ResponseEntity<?> updateNotificationType(@RequestBody NotificationType notificationType,@PathVariable String notificationTypeIdString)
    {
        try {
            Long notificationTypeId = Long.parseLong(notificationTypeIdString);
            NotificationType notificationTypeToUpdate=notificationTypeService.getNotificationTypeById(notificationTypeId);
            if (notificationTypeToUpdate == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            NotificationType updatedNotification= notificationTypeService.updateNotificationType(notificationTypeId,notificationType);
            return ResponseService.generateSuccessResponse("Notification type is updated successfully ", updatedNotification,HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            exceptionHandlingService.handleException(e);
            return ResponseService.generateErrorResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-filter-notification-types")
    public ResponseEntity<?> getFilterNotificationTypes(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            List<NotificationType> notificationTypes = notificationTypeService.notificationTypeFilter();

            if (notificationTypes.isEmpty()) {
                return ResponseService.generateSuccessResponse("No notification types found", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = notificationTypes.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more notification types available");
            }
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<NotificationType> paginatedList = notificationTypes.subList(fromIndex, toIndex);

            // Construct response
            Map<String, Object> response = new HashMap<>();
            response.put("notificationTypes", paginatedList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("Notification Types Retrieved Successfully", response, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
