package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.FileTypeService;
import com.shodhAI.ShodhAI.Service.GenderService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/file-type", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class FileTypeController {

    @Autowired
    EntityManager entityManager;

    @Autowired
    FileTypeService fileTypeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional
    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllFileType(HttpServletRequest request) {
        try {

            List<FileType> fileTypeList = fileTypeService.getAllFileType();
            if (fileTypeList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("File Type Retrieved Successfully", fileTypeList, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse("Exception Caught: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @GetMapping("/get-file-type-by-id/{fileTypeIdString}")
    public ResponseEntity<?> retrieveFileTypeById(HttpServletRequest request, @PathVariable String fileTypeIdString) {
        try {

            Long genderId = Long.parseLong(fileTypeIdString);
            FileType fileType = fileTypeService.getFileTypeById(genderId);
            if (fileType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            return ResponseService.generateSuccessResponse("File Type Retrieved Successfully", fileType, HttpStatus.OK);

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
