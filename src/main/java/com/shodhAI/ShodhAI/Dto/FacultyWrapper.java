package com.shodhAI.ShodhAI.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Role;

import java.util.Date;

public class FacultyWrapper {

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

    @JsonProperty("profile_picture_url")
    private String profilePictureUrl;

    public void wrapDetails(Faculty faculty) {

        this.id = faculty.getId();
        this.archived = faculty.getArchived();
        this.firstName = faculty.getFirstName();
        this.lastName = faculty.getLastName();
        if (faculty.getLastName() != null) {
            this.fullName = faculty.getFirstName() + " " + faculty.getLastName();
        } else {
            this.fullName = faculty.getFirstName();
        }
        this.collegeEmail = faculty.getCollegeEmail();
        this.personalEmail = faculty.getPersonalEmail();
        this.countryCode = faculty.getCountryCode();
        this.mobileNumber = faculty.getMobileNumber();
        this.dateOfBirth = faculty.getDateOfBirth();
        this.role = faculty.getRole();
        this.gender = faculty.getGender();
        this.userName = faculty.getUserName();
        this.createdDate = faculty.getCreatedDate();
        this.updatedDate = faculty.getUpdatedDate();
        this.profilePictureUrl = faculty.getProfilePictureUrl();
    }

}
