package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSemesterDegreeDto {
    @JsonProperty("course_id")
    private Long courseId;
    
    @JsonProperty("semester_id")
    private Long semesterId;
    
    @JsonProperty("academic_degree_id")
    private Long academicDegreeId;
    
    // Getters and setters with proper naming to match JsonProperty
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public Long getSemesterId() {
        return semesterId;
    }
    
    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }
    
    public Long getAcademicDegreeId() {
        return academicDegreeId;
    }
    
    public void setAcademicDegreeId(Long academicDegreeId) {
        this.academicDegreeId = academicDegreeId;
    }
}