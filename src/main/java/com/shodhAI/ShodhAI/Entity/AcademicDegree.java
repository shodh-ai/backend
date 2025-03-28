package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @JsonIgnore
    @ManyToMany(mappedBy = "academicDegrees")
    private List<Semester> semesters;

    @OneToMany(mappedBy = "academicDegree")
    @JsonIgnore
    private List<CourseSemesterDegree> courseSemesterDegrees;

    public AcademicDegree(Long degreeId, @NonNull String degreeName, String programName, String institutionName, @NonNull Character archived, Date createdDate) {
        this.degreeId = degreeId;
        this.degreeName = degreeName;
        this.programName = programName;
        this.institutionName = institutionName;
        this.archived = archived;
        this.createdDate = createdDate;
    }
}
