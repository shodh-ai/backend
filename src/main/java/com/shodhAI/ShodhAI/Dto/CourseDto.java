package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {

    @JsonProperty("course_title")
    private String courseTitle;

    @JsonProperty("course_description")
    private String courseDescription;

    @JsonProperty("course_duration")
    private String courseDuration;

    @JsonProperty("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date startDate;

    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date endDate;

    @JsonProperty("students")
    private List<Student> students = new ArrayList<>();

    @JsonProperty("faculty_members")
    private List<Faculty> facultyMembers = new ArrayList<>();
}
