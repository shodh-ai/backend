package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Entity
@Table(name = "student")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    @JsonProperty("student_id")
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
    @NotNull
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
    @NotNull
    @JoinColumn(name = "gender_id")
    @JsonProperty("gender")
    private Gender gender;

    @Column(name = "token", columnDefinition = "TEXT", unique = true)
    @JsonProperty("token")
    private String token;

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

    @ManyToOne
    @NotNull
    @JoinColumn(name = "academic_degree_id")
    @JsonProperty("academic_degree")
    private AcademicDegree academicDegree;

    @Column(name = "marks_obtained")
    @JsonProperty("marks_obtained")
    private Double marksObtained = 0.0;

    @Column(name = "total_marks")
    @JsonProperty("total_marks")
    private Double totalMarks = 0.0;

    @OneToOne
    @NotNull
    @JoinColumn(name = "accuracy_id")
    @JsonProperty("accuracy_id")
    private Accuracy accuracy;

    @OneToOne
    @NotNull
    @JoinColumn(name = "critical_thinking_id")
    @JsonProperty("critical_thinking_id")
    private CriticalThinking criticalThinking;

}
