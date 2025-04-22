package com.shodhAI.ShodhAI.Controller;

import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.FileTypeService;
import com.shodhAI.ShodhAI.Service.ResponseService;
import com.shodhAI.ShodhAI.Service.SanitizerService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/file-type", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class FileTypeController {

    @Autowired
    EntityManager entityManager;

    @Autowired
    FileTypeService fileTypeService;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    SanitizerService sanitizerService;

    @Transactional
    @GetMapping("/get-all")
    public ResponseEntity<?> retrieveAllFileType(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int limit, HttpServletRequest request) {
        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }
            List<FileType> fileTypeList = fileTypeService.getAllFileType();
            if (fileTypeList.isEmpty()) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            int totalItems = fileTypeList.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more file types available");
            }

            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<FileType> fileTypes = fileTypeList.subList(fromIndex, toIndex);

            Map<String, Object> response = new HashMap<>();
            response.put("fileTypes", fileTypes);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);
            return ResponseService.generateSuccessResponse("File Type Retrieved Successfully", response, HttpStatus.OK);

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

    @PostMapping("/add")
    public ResponseEntity<?> addFileType(@RequestBody FileType fileType)
    {
        try
        {
            sanitizerService.sanitizeInputMap(List.of(fileType));
            FileType fileTypeToSave=fileTypeService.addFileType(fileType);
            return ResponseService.generateSuccessResponse("File type is successfully added",fileTypeToSave, HttpStatus.CREATED);
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

    @DeleteMapping("/delete/{fileTypeIdString}")
    public ResponseEntity<?> deleteFileTpe (@PathVariable String fileTypeIdString)
    {
        try
        {
            Long fileTypeId = Long.parseLong(fileTypeIdString);
            FileType fileType = fileTypeService.getFileTypeById(fileTypeId);
            if (fileType == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            FileType deletedFileType =fileTypeService.deleteFileTypeById(fileTypeId);
            return ResponseService.generateSuccessResponse("File type is archived successfully",deletedFileType ,HttpStatus.OK);
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

    @PatchMapping("/update/{fileTypeIdString}")
    public ResponseEntity<?> updateFileType(@RequestBody FileType fileType, @PathVariable String fileTypeIdString) throws Exception, RuntimeException,DataIntegrityViolationException {
        try {

            sanitizerService.sanitizeInputMap(List.of(fileType));
            Long fileTypeId = Long.parseLong(fileTypeIdString);
            FileType fileTypeToFound=fileTypeService.getFileTypeById(fileTypeId);
            if (fileTypeToFound == null) {
                return ResponseService.generateErrorResponse("Data not present in the DB", HttpStatus.OK);
            }
            FileType updatedFile= fileTypeService.updateFileType(fileTypeId,fileType);
            return ResponseService.generateSuccessResponse("File type is updated successfully ", updatedFile,HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e)
        {
            exceptionHandlingService.handleException(e);
            throw new DataIntegrityViolationException(e.getMessage());
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

    @GetMapping("/get-filter-file-types")
    public ResponseEntity<?> getFilterFileTypes(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            if (offset < 0) {
                throw new IllegalArgumentException("Offset for pagination cannot be a negative number");
            }
            if (limit <= 0) {
                throw new IllegalArgumentException("Limit for pagination cannot be a negative number or 0");
            }

            List<FileType> fileTypes = fileTypeService.fileTypeFilter();

            if (fileTypes.isEmpty()) {
                return ResponseService.generateSuccessResponse("No file types found", new ArrayList<>(), HttpStatus.OK);
            }

            // Pagination logic
            int totalItems = fileTypes.size();
            int totalPages = (int) Math.ceil((double) totalItems / limit);
            int fromIndex = offset * limit;
            int toIndex = Math.min(fromIndex + limit, totalItems);

            if (offset >= totalPages && offset != 0) {
                throw new IllegalArgumentException("No more file types available");
            }
            if (fromIndex >= totalItems) {
                return ResponseService.generateErrorResponse("Page index out of range", HttpStatus.BAD_REQUEST);
            }

            List<FileType> paginatedList = fileTypes.subList(fromIndex, toIndex);

            // Construct response
            Map<String, Object> response = new HashMap<>();
            response.put("fileTypes", paginatedList);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("currentPage", offset);

            return ResponseService.generateSuccessResponse("File Types Retrieved Successfully", response, HttpStatus.OK);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            return ResponseService.generateErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
