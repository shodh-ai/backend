package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.Module;
import com.shodhAI.ShodhAI.Entity.CourseSemesterDegree;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Entity.Student;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ModuleTest {
    private Long moduleId;
    private String moduleTitle;
    private String moduleDescription;
    private String moduleDuration;
    private Character archived;
    private Date createdDate;
    private Date updatedDate;
    private Course course;


    @BeforeEach
    void setUp() {
        moduleId = 1L;
        moduleTitle = "moduleName";
        moduleDescription = "moduleDescription";
        moduleDuration = "moduleDuration";
        archived = 'N';
        course= new Course();
        createdDate= new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testModuleConstructor")
    void testModuleConstructor(){
        Module moduleByConstructor =  Module.builder().moduleId(moduleId).moduleTitle(moduleTitle).moduleDescription(moduleDescription).archived(archived).moduleDuration(moduleDuration).createdDate(createdDate).updatedDate(updatedDate).course(course).build();

        assertEquals(moduleId, moduleByConstructor.getModuleId());
        assertEquals(moduleTitle, moduleByConstructor.getModuleTitle());
        assertEquals(moduleDescription, moduleByConstructor.getModuleDescription());
        assertEquals(moduleDuration, moduleByConstructor.getModuleDuration());
        assertEquals(archived, moduleByConstructor.getArchived());
        assertEquals(createdDate, moduleByConstructor.getCreatedDate());
        assertEquals(updatedDate, moduleByConstructor.getUpdatedDate());
        assertEquals(course, moduleByConstructor.getCourse());
    }

    @Test
    @DisplayName("testModuleSettersAndGetters")
    void testModuleSettersAndGetters(){
        Module module = getModule();
        assertEquals(moduleId, module.getModuleId());
        assertEquals(moduleTitle, module.getModuleTitle());
        assertEquals(moduleDescription, module.getModuleDescription());
        assertEquals(moduleDuration, module.getModuleDuration());
        assertEquals(archived, module.getArchived());
        assertEquals(course, module.getCourse());
        assertEquals(createdDate, module.getCreatedDate());
        assertEquals(updatedDate, module.getUpdatedDate());
    }

    private @NotNull Module getModule() {
        Module module = new Module();
        module.setModuleId(moduleId);
        module.setModuleTitle(moduleTitle);
        module.setModuleDescription(moduleDescription);
        module.setModuleDuration(moduleDuration);
        module.setArchived(archived);
        module.setCourse(course);
        module.setCreatedDate(createdDate);
        module.setUpdatedDate(updatedDate);
        return module;
    }
}


