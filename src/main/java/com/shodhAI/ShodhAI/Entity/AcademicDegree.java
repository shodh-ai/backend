package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;
import java.util.List;
@Builder
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "degreeId")
@Entity
@Table(name = "academic_degree")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicDegree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "degree_id")
    private Long degreeId;

    @NonNull
    @Column(name = "degree_name")
    @JsonProperty("degree_name")
    @Pattern(regexp = "^[A-Za-z].*", message = "Degree name must start with an alphabet.")
    private String degreeName;

    @Column(name = "program_name")
    @JsonProperty("program_name")
    private String programName;

    @Column(name = "institution_name")
    @JsonProperty("institution_name")
    private String institutionName;

    @NonNull
    @Column(name = "archived")
    @JsonProperty("archived")
    private Character archived = 'N';

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

    @JsonManagedReference("semesters-degree")
    @ManyToMany(mappedBy = "academicDegrees")
    private List<Semester> semesters;

    @OneToMany(mappedBy = "academicDegree")
    @JsonIgnore
    private List<CourseSemesterDegree> courseSemesterDegrees;

    @ManyToMany(mappedBy = "degrees")
    @JsonManagedReference("institute-degree")
    private List<Institute> institutes;

    @OneToMany(mappedBy = "academicDegree", cascade = CascadeType.ALL) // remove orphanRemoval
    @JsonManagedReference(value = "academic-degree-course")
    @JsonProperty("courses")
    private List<Course> courses;

    public AcademicDegree(Long degreeId, @NonNull String degreeName, String programName, String institutionName, @NonNull Character archived, Date createdDate) {
        this.degreeId = degreeId;
        this.degreeName = degreeName;
        this.programName = programName;
        this.institutionName = institutionName;
        this.archived = archived;
        this.createdDate = createdDate;
    }
}
