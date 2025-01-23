package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.Accuracy;
import com.shodhAI.ShodhAI.Entity.CriticalThinking;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;

import java.util.Date;

public class StudentWrapper {

    @JsonProperty("student_id")
    private Long id;

    @JsonProperty("archived")
    private Character archived = 'N';

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("college_email")
    private String collegeEmail;

    @JsonProperty("personal_email")
    private String personalEmail;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("created_date")
    private Date createdDate;

    @JsonProperty("modified_date")
    private Date updatedDate;

    @JsonProperty("academic_degree")
    private AcademicDegree academicDegree;

    @JsonProperty("marks_obtained")
    private Double marksObtained;

    @JsonProperty("total_marks")
    private Double totalMarks;

    @JsonProperty("overall_score")
    private Double overallScore;

    @JsonProperty("accuracy")
    private Accuracy accuracy;

    @JsonProperty("critical_thinking")
    private CriticalThinking criticalThinking;

    public void wrapDetails(Student student) {

        this.id = student.getId();
        this.archived = student.getArchived();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        if (student.getLastName() != null) {
            this.fullName = student.getFirstName() + " " + student.getLastName();
        } else {
            this.fullName = student.getFirstName();
        }
        this.collegeEmail = student.getCollegeEmail();
        this.personalEmail = student.getPersonalEmail();
        this.countryCode = student.getCountryCode();
        this.mobileNumber = student.getMobileNumber();
        this.dateOfBirth = student.getDateOfBirth();
        this.role = student.getRole();
        this.gender = student.getGender();
        this.userName = student.getUserName();
        this.createdDate = student.getCreatedDate();
        this.updatedDate = student.getUpdatedDate();
        this.academicDegree = student.getAcademicDegree();
        this.marksObtained = student.getMarksObtained();
        this.totalMarks = student.getTotalMarks();
        if (student.getTotalMarks() == 0.0) {
            this.overallScore = 0.0;
        } else {
            this.overallScore = student.getMarksObtained()/student.getTotalMarks();
        }
        this.accuracy = student.getAccuracy();
        this.criticalThinking = student.getCriticalThinking();
    }

}
