package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileTypeTest {
    private Long fileTypeId;
    private String fileTypeName;
    private Character archived;

    @BeforeEach
    void setUp() {
        fileTypeId = 1L;
        fileTypeName = "fileTypeName";
        archived = 'N';
    }

    @Test
    @DisplayName("testFileTypeConstructor")
    void testFileTypeConstructor(){
        FileType fileTypeByConstructor =  FileType.builder().fileTypeId(fileTypeId).fileTypeName(fileTypeName).archived(archived).build();

        assertEquals(fileTypeId, fileTypeByConstructor.getFileTypeId());
        assertEquals(fileTypeName, fileTypeByConstructor.getFileTypeName());
        assertEquals(archived, fileTypeByConstructor.getArchived());
    }

    @Test
    @DisplayName("testFileTypeSettersAndGetters")
    void testFileTypeSettersAndGetters(){
        FileType fileType = new FileType();
        fileType.setFileTypeId(fileTypeId);
        fileType.setFileTypeName(fileTypeName);
        fileType.setArchived(archived);
        assertEquals(fileTypeId, fileType.getFileTypeId());
        assertEquals(fileTypeName, fileType.getFileTypeName());
        assertEquals(archived, fileType.getArchived());
    }
}


