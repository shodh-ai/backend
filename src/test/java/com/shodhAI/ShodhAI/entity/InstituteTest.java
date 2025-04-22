package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Institute;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Student;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstituteTest {
    private Long instituteId;
    private String instituteName;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private List<AcademicDegree> degrees;

    @BeforeEach
    void setUp() {
        instituteId = 1L;
        instituteName = "instituteName";
        archived = 'N';
        degrees = new ArrayList<>() {};
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testInstituteConstructor")
    void testInstituteConstructor(){
        Institute instituteByConstructor =  Institute.builder().instituteId(instituteId).institutionName(instituteName).archived(archived).createdDate(createdDate).updatedDate(updatedDate).degrees(degrees).build();

        assertEquals(instituteId, instituteByConstructor.getInstituteId());
        assertEquals(instituteName, instituteByConstructor.getInstitutionName());
        assertEquals(archived, instituteByConstructor.getArchived());
        assertEquals(createdDate, instituteByConstructor.getCreatedDate());
        assertEquals(updatedDate, instituteByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testInstituteSettersAndGetters")
    void testInstituteSettersAndGetters(){
        Institute institute = getInstitute();
        assertEquals(instituteId, institute.getInstituteId());
        assertEquals(instituteName, institute.getInstitutionName());
        assertEquals(archived, institute.getArchived());
        assertEquals(degrees, institute.getDegrees());
        assertEquals(createdDate, institute.getCreatedDate());
        assertEquals(updatedDate, institute.getUpdatedDate());
    }

    private @NotNull Institute getInstitute() {
        Institute institute = new Institute();
        institute.setInstituteId(instituteId);
        institute.setInstitutionName(instituteName);
        institute.setArchived(archived);
        institute.setDegrees(degrees);
        institute.setCreatedDate(createdDate);
        institute.setUpdatedDate(updatedDate);
        return institute;
    }
}

