package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.FileTypeController;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Service.FileTypeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestSecurityConfig.class, TestJwtConfig.class})
@WebMvcTest(FileTypeController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class FileTypeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SanitizerService sanitizerService;

    @MockBean
    private FileTypeService fileTypeService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllFileTypes")
    void testGetAllFileTypes() throws Exception {
        // Create sample file types
        List<FileType> fileTypes = new ArrayList<>();
        FileType type1 = new FileType();
        type1.setFileTypeId(1L);
        type1.setFileTypeName("Article");

        FileType type2 = new FileType();
        type2.setFileTypeId(2L);
        type2.setFileTypeName("Video");

        fileTypes.add(type1);
        fileTypes.add(type2);

        // Mock service call
        when(fileTypeService.getAllFileType()).thenReturn(fileTypes);

        // Perform GET request
        mockMvc.perform(get("/file-type/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("File Type Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(fileTypeService).getAllFileType();
    }

    @Test
    @DisplayName("testGetAllFileTypesEmptyList")
    void testGetAllFileTypesEmptyList() throws Exception {
        // Mock empty list response
        when(fileTypeService.getAllFileType()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/file-type/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(fileTypeService).getAllFileType();
    }

    @Test
    @DisplayName("testGetAllFileTypesInvalidPagination")
    void testGetAllFileTypesInvalidPagination() throws Exception {
        // Perform GET request with invalid pagination parameters
        mockMvc.perform(get("/file-type/get-all")
                        .param("offset", "-1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Exception Caught: Offset for pagination cannot be a negative number"));

        // Verify exception handling service was called
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testGetFileTypeById")
    void testGetFileTypeById() throws Exception {
        // Create sample file type
        FileType fileType = new FileType();
        fileType.setFileTypeId(1L);
        fileType.setFileTypeName("Article");

        // Mock service call
        when(fileTypeService.getFileTypeById(1L)).thenReturn(fileType);

        // Perform GET request
        mockMvc.perform(get("/file-type/get-file-type-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("File Type Retrieved Successfully"))
                .andExpect(jsonPath("$.data.file_type_id").value(1L));


        // Verify service method was called
        verify(fileTypeService).getFileTypeById(1L);
    }

    @Test
    @DisplayName("testGetFileTypeByIdNotFound")
    void testGetFileTypeByIdNotFound() throws Exception {
        // Mock not found scenario
        when(fileTypeService.getFileTypeById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/file-type/get-file-type-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(fileTypeService).getFileTypeById(999L);
    }

    @Test
    @DisplayName("testAddFileType")
    void testAddFileType() throws Exception {
        // Create a sample FileType
        FileType fileType = new FileType();
        fileType.setFileTypeName("Article");

        FileType savedFileType = new FileType();
        savedFileType.setFileTypeId(1L);
        savedFileType.setFileTypeName("Article");

        // Mock service call
        when(fileTypeService.addFileType(any(FileType.class))).thenReturn(savedFileType);

        // Perform POST request
        mockMvc.perform(post("/file-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileType)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("File type is successfully added"))
                .andExpect(jsonPath("$.data.file_type_id").value(1L));

        // Verify service method was called
        verify(fileTypeService).addFileType(any(FileType.class));
    }

    @Test
    @DisplayName("testAddFileTypeIllegalArgumentException")
    void testAddFileTypeIllegalArgumentException() throws Exception {
        // Create a sample FileType
        FileType fileType = new FileType();
        fileType.setFileTypeName(""); // Invalid name

        // Mock service exception
        when(fileTypeService.addFileType(any(FileType.class)))
                .thenThrow(new IllegalArgumentException("File type name cannot be empty"));

        // Perform POST request
        mockMvc.perform(post("/file-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileType)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("File type name cannot be empty"));

        // Verify exception handling
        verify(fileTypeService).addFileType(any(FileType.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testUpdateFileType")
    void testUpdateFileType() throws Exception {
        // Create a sample FileType for update
        FileType fileType = new FileType();
        fileType.setFileTypeName("Updated Article");

        FileType existingFileType = new FileType();
        existingFileType.setFileTypeId(1L);
        existingFileType.setFileTypeName("Article");

        FileType updatedFileType = new FileType();
        updatedFileType.setFileTypeId(1L);
        updatedFileType.setFileTypeName("Updated Article");

        // Mock service calls
        when(fileTypeService.getFileTypeById(1L)).thenReturn(existingFileType);
        when(fileTypeService.updateFileType(eq(1L), any(FileType.class))).thenReturn(updatedFileType);

        // Perform PATCH request
        mockMvc.perform(patch("/file-type/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("File type is updated successfully "))
                .andExpect(jsonPath("$.data.file_type_id").value(1L))
                .andExpect(jsonPath("$.data.file_type_name").value("Updated Article"));

        // Verify service methods were called
        verify(fileTypeService).getFileTypeById(1L);
        verify(fileTypeService).updateFileType(eq(1L), any(FileType.class));
    }

    @Test
    @DisplayName("testUpdateFileTypeNotFound")
    void testUpdateFileTypeNotFound() throws Exception {
        // Create a sample FileType for update
        FileType fileType = new FileType();
        fileType.setFileTypeName("Updated Article");

        // Mock service call - File type not found
        when(fileTypeService.getFileTypeById(999L)).thenReturn(null);

        // Perform PATCH request
        mockMvc.perform(patch("/file-type/update/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(fileTypeService).getFileTypeById(999L);
        verify(fileTypeService, never()).updateFileType(eq(999L), any(FileType.class));
    }

    @Test
    @DisplayName("testDeleteFileType")
    void testDeleteFileType() throws Exception {
        // Create sample file type
        FileType fileType = new FileType();
        fileType.setFileTypeId(1L);
        fileType.setFileTypeName("Article");

        // Mock service calls
        when(fileTypeService.getFileTypeById(1L)).thenReturn(fileType);
        when(fileTypeService.deleteFileTypeById(1L)).thenReturn(fileType);

        // Perform DELETE request
        mockMvc.perform(delete("/file-type/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("File type is archived successfully"))
                .andExpect(jsonPath("$.data.file_type_id").value(1L));

        // Verify service methods were called
        verify(fileTypeService).getFileTypeById(1L);
        verify(fileTypeService).deleteFileTypeById(1L);
    }

    @Test
    @DisplayName("testDeleteFileTypeNotFound")
    void testDeleteFileTypeNotFound() throws Exception {
        // Mock not found scenario
        when(fileTypeService.getFileTypeById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/file-type/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service methods called
        verify(fileTypeService).getFileTypeById(999L);
        verify(fileTypeService, never()).deleteFileTypeById(999L);
    }

    @Test
    @DisplayName("testGetFilterFileTypes")
    void testGetFilterFileTypes() throws Exception {
        // Create sample file types
        List<FileType> fileTypes = new ArrayList<>();
        FileType type1 = new FileType();
        type1.setFileTypeId(1L);
        type1.setFileTypeName("Article");

        FileType type2 = new FileType();
        type2.setFileTypeId(2L);
        type2.setFileTypeName("Video");

        fileTypes.add(type1);
        fileTypes.add(type2);

        // Mock service call
        when(fileTypeService.fileTypeFilter()).thenReturn(fileTypes);

        // Perform GET request
        mockMvc.perform(get("/file-type/get-filter-file-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("File Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(fileTypeService).fileTypeFilter();
    }

    @Test
    @DisplayName("testGetFilterFileTypesEmptyList")
    void testGetFilterFileTypesEmptyList() throws Exception {
        // Mock empty list response
        when(fileTypeService.fileTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/file-type/get-filter-file-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No file types found"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service method was called
        verify(fileTypeService).fileTypeFilter();
    }
}
