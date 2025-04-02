package com.shodhAI.ShodhAI.Dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SemesterDto

{
    @JsonProperty("semester_id")
    private Long semesterId;

    @JsonProperty("semester_name")
    private String semesterName;

    @JsonProperty("academic_degree_ids")
    private List<Long> academicDegreeIds;

    @JsonProperty("course_ids")
    private List<Long> courseIds;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("course_associations")
    private List<CourseSemesterDegreeDto> courseAssociations;
}
