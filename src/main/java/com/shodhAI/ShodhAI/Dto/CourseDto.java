package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("course_id")
    private Long courseId;

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

    @JsonProperty("student_ids")
    private List<Long> studentIds = new ArrayList<>();

    @JsonProperty("faculty_member_ids")
    private List<Long> facultyMemberIds = new ArrayList<>();

    @JsonProperty("semester_id")
    private Long semesterId;

    @JsonProperty("course_semester_degree_associations")
    private List<CourseSemesterDegreeDto> courseSemesterDegreeAssociations;

    @JsonProperty("module_ids")
    private List<Long> moduleIds;

    @JsonProperty("academic_degree_id")
    private Long academicDegreeId;

}
