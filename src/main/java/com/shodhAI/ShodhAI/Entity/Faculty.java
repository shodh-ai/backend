package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "faculty")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faculty_id")
    @JsonProperty("faculty_id")
    private Long id;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;

    @Email
    @Column(name = "college_email", unique = true)
    @JsonProperty("college_email")
    private String collegeEmail;

    @Email
    @NotNull
    @Column(name = "personal_email")
    @JsonProperty("personal_email")
    private String personalEmail;

    @Column(name = "country_code")
    @JsonProperty("country_code")
    @Pattern(regexp = "^[+][1-9]{1,3}$", message = "Country code must start with a '+' followed by 1 to 3 digits.")
    private String countryCode;

    @Nullable
    @Column(name = "mobile_number", unique = true)
    @JsonProperty("mobile_number")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be a valid 10-digit number.")
    private String mobileNumber;

    @Column(name = "date_of_birth")
    @JsonProperty("date_of_birth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date dateOfBirth;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "role_id")
    @JsonProperty("role")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    @JsonProperty("gender")
    private Gender gender;

    @JsonIgnore
    @Column(name = "token", columnDefinition = "TEXT", unique = true)
    @JsonProperty("token")
    private String token;

    @Column(name = "otp")
    @JsonProperty("otp")
    private String otp;

    @Column(name = "user_name", unique = true)
    @JsonProperty("user_name")
    private String userName;

    @Column(name = "password")
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date createdDate;

    @Column(name = "modified_date")
    @JsonProperty("modified_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    // TODO (MIGHT HAVE TO CHANGE IN FUTURE) this won't work with instant as instant does not have calendar features like LocalDateTime etc.
    private Date updatedDate;

    @Column(name = "profile_picture_url")
    @JsonProperty("profile_picture_url")
    private String profilePictureUrl;

    // Many-to-Many relationship with Course
    @ManyToMany(mappedBy = "facultyMembers",cascade = CascadeType.ALL)
    @JsonBackReference("courses-faculty-reference")
    @JsonProperty("courses")
    private List<Course> courses = new ArrayList<>();

    // Many-to-Many relationship with Student
    @ManyToMany
    @JoinTable(
            name = "faculty_student",
            joinColumns = @JoinColumn(name = "faculty_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonProperty("students")
    @JsonBackReference("students-faculty-reference")
    private List<Student> students = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    @JsonProperty("notifications")
    private List<Notification> notifications;

}


