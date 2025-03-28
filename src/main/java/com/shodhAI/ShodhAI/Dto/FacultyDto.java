package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Course;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyDto {

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Email
    @Column(name = "college_email")
    @JsonProperty("college_email")
    private String collegeEmail;

    @Email
    @Column(name = "personal_email")
    @JsonProperty("personal_email")
    private String personalEmail;

    @JsonProperty("country_code")
    private String countryCode = "+91";

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("date_of_birth")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date dateOfBirth;

    @JsonProperty("role_id")
    @JsonIgnore
    private Long roleId;

    @JsonProperty("gender_id")
    private Long genderId;

    @JsonProperty("user_name")
    private String userName;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+=\\-{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one uppercase letter, and one special character."
    )
    @JsonProperty(value = "password")
    private String password;

    @JsonProperty("profile_picture_url")
    private String profilePictureUrl;

    @JsonProperty("course_ids")
    private List<Long> courseIds = new ArrayList<>();

}
