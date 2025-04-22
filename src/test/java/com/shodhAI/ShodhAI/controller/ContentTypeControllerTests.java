package com.shodhAI.ShodhAI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shodhAI.ShodhAI.Controller.ContentTypeController;
import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Service.ContentTypeService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
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
@WebMvcTest(ContentTypeController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = TestSecurityConfig.class)
public class ContentTypeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContentTypeService contentTypeService;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private ExceptionHandlingService exceptionHandlingService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("testGetAllContentTypes")
    void testGetAllContentTypes() throws Exception {
        // Create sample content types
        List<ContentType> contentTypes = new ArrayList<>();
        ContentType type1 = new ContentType();
        type1.setContentTypeId(1L);
        type1.setContentTypeName("Article");

        ContentType type2 = new ContentType();
        type2.setContentTypeId(2L);
        type2.setContentTypeName("Video");

        contentTypes.add(type1);
        contentTypes.add(type2);

        // Mock service call
        when(contentTypeService.getAllContentType()).thenReturn(contentTypes);

        // Perform GET request
        mockMvc.perform(get("/content-type/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Content Type Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(contentTypeService).getAllContentType();
    }

    @Test
    @DisplayName("testGetAllContentTypesEmptyList")
    void testGetAllContentTypesEmptyList() throws Exception {
        // Mock empty list response
        when(contentTypeService.getAllContentType()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/content-type/get-all")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service method was called
        verify(contentTypeService).getAllContentType();
    }

    @Test
    @DisplayName("testGetAllContentTypesInvalidPagination")
    void testGetAllContentTypesInvalidPagination() throws Exception {
        // Perform GET request with invalid pagination parameters
        mockMvc.perform(get("/content-type/get-all")
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
    @DisplayName("testGetContentTypeById")
    void testGetContentTypeById() throws Exception {
        // Create sample content type
        ContentType contentType = new ContentType();
        contentType.setContentTypeId(1L);
        contentType.setContentTypeName("Article");

        // Mock service call
        when(contentTypeService.getContentTypeById(1L)).thenReturn(contentType);

        // Perform GET request
        mockMvc.perform(get("/content-type/get-content-type-by-id/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Content Type Retrieved Successfully"))
                .andExpect(jsonPath("$.data.content_type_id").value(1L));


        // Verify service method was called
        verify(contentTypeService).getContentTypeById(1L);
    }

    @Test
    @DisplayName("testGetContentTypeByIdNotFound")
    void testGetContentTypeByIdNotFound() throws Exception {
        // Mock not found scenario
        when(contentTypeService.getContentTypeById(999L)).thenReturn(null);

        // Perform GET request
        mockMvc.perform(get("/content-type/get-content-type-by-id/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));
        // Verify service method was called
        verify(contentTypeService).getContentTypeById(999L);
    }

    @Test
    @DisplayName("testAddContentType")
    void testAddContentType() throws Exception {
        // Create a sample ContentType
        ContentType contentType = new ContentType();
        contentType.setContentTypeName("Article");

        ContentType savedContentType = new ContentType();
        savedContentType.setContentTypeId(1L);
        savedContentType.setContentTypeName("Article");

        // Mock service call
        when(contentTypeService.addContentType(any(ContentType.class))).thenReturn(savedContentType);

        // Perform POST request
        mockMvc.perform(post("/content-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(contentType)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Content type is successfully added"))
                .andExpect(jsonPath("$.data.content_type_id").value(1L));

        // Verify service method was called
        verify(contentTypeService).addContentType(any(ContentType.class));
    }

    @Test
    @DisplayName("testAddContentTypeIllegalArgumentException")
    void testAddContentTypeIllegalArgumentException() throws Exception {
        // Create a sample ContentType
        ContentType contentType = new ContentType();
        contentType.setContentTypeName(""); // Invalid name

        // Mock service exception
        when(contentTypeService.addContentType(any(ContentType.class)))
                .thenThrow(new IllegalArgumentException("Content type name cannot be empty"));

        // Perform POST request
        mockMvc.perform(post("/content-type/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(contentType)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Content type name cannot be empty"));

        // Verify exception handling
        verify(contentTypeService).addContentType(any(ContentType.class));
        verify(exceptionHandlingService).handleException(any(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("testUpdateContentType")
    void testUpdateContentType() throws Exception {
        // Create a sample ContentType for update
        ContentType contentType = new ContentType();
        contentType.setContentTypeName("Updated Article");

        ContentType existingContentType = new ContentType();
        existingContentType.setContentTypeId(1L);
        existingContentType.setContentTypeName("Article");

        ContentType updatedContentType = new ContentType();
        updatedContentType.setContentTypeId(1L);
        updatedContentType.setContentTypeName("Updated Article");

        // Mock service calls
        when(contentTypeService.getContentTypeById(1L)).thenReturn(existingContentType);
        when(contentTypeService.updateContentType(eq(1L), any(ContentType.class))).thenReturn(updatedContentType);

        // Perform PATCH request
        mockMvc.perform(patch("/content-type/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(contentType)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Content type is updated successfully "))
                .andExpect(jsonPath("$.data.content_type_id").value(1L))
                .andExpect(jsonPath("$.data.content_type_name").value("Updated Article"));

        // Verify service methods were called
        verify(contentTypeService).getContentTypeById(1L);
        verify(contentTypeService).updateContentType(eq(1L), any(ContentType.class));
    }

        @Test
        @DisplayName("testUpdateContentTypeNotFound")
        void testUpdateContentTypeNotFound() throws Exception {
            // Create a sample ContentType for update
            ContentType contentType = new ContentType();
            contentType.setContentTypeName("Updated Article");

            // Mock service call - Content type not found
            when(contentTypeService.getContentTypeById(999L)).thenReturn(null);

            // Perform PATCH request
            mockMvc.perform(patch("/content-type/update/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(contentType)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"))
                    .andExpect(jsonPath("$.message").value("Data not present in the DB"));

            // Verify service method was called
            verify(contentTypeService).getContentTypeById(999L);
            verify(contentTypeService, never()).updateContentType(eq(999L), any(ContentType.class));
        }

    @Test
    @DisplayName("testDeleteContentType")
    void testDeleteContentType() throws Exception {
        // Create sample content type
        ContentType contentType = new ContentType();
        contentType.setContentTypeId(1L);
        contentType.setContentTypeName("Article");

        // Mock service calls
        when(contentTypeService.getContentTypeById(1L)).thenReturn(contentType);
        when(contentTypeService.deleteContentTypeById(1L)).thenReturn(contentType);

        // Perform DELETE request
        mockMvc.perform(delete("/content-type/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Content type is archived successfully"))
                .andExpect(jsonPath("$.data.content_type_id").value(1L));

        // Verify service methods were called
        verify(contentTypeService).getContentTypeById(1L);
        verify(contentTypeService).deleteContentTypeById(1L);
    }

    @Test
    @DisplayName("testDeleteContentTypeNotFound")
    void testDeleteContentTypeNotFound() throws Exception {
        // Mock not found scenario
        when(contentTypeService.getContentTypeById(999L)).thenReturn(null);

        // Perform DELETE request
        mockMvc.perform(delete("/content-type/delete/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Data not present in the DB"));

        // Verify service methods called
        verify(contentTypeService).getContentTypeById(999L);
        verify(contentTypeService, never()).deleteContentTypeById(999L);
    }

    @Test
    @DisplayName("testGetFilterContentTypes")
    void testGetFilterContentTypes() throws Exception {
        // Create sample content types
        List<ContentType> contentTypes = new ArrayList<>();
        ContentType type1 = new ContentType();
        type1.setContentTypeId(1L);
        type1.setContentTypeName("Article");

        ContentType type2 = new ContentType();
        type2.setContentTypeId(2L);
        type2.setContentTypeName("Video");

        contentTypes.add(type1);
        contentTypes.add(type2);

        // Mock service call
        when(contentTypeService.contentTypeFilter()).thenReturn(contentTypes);

        // Perform GET request
        mockMvc.perform(get("/content-type/get-filter-content-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Content Types Retrieved Successfully"))
                .andExpect(jsonPath("$.data.totalItems").value(2))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        // Verify service method was called
        verify(contentTypeService).contentTypeFilter();
    }

    @Test
    @DisplayName("testGetFilterContentTypesEmptyList")
    void testGetFilterContentTypesEmptyList() throws Exception {
        // Mock empty list response
        when(contentTypeService.contentTypeFilter()).thenReturn(new ArrayList<>());

        // Perform GET request
        mockMvc.perform(get("/content-type/get-filter-content-types")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("No content types found"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());

        // Verify service method was called
        verify(contentTypeService).contentTypeFilter();
    }
}