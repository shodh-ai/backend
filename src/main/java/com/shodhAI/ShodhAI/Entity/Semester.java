package com.shodhAI.ShodhAI.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "semester")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    @Id
    @JsonProperty("semester_id")
    private Long semesterId;

    @Column(name = "semester_name", nullable = false)
    @JsonProperty("semester_name")
    private String semesterName;

    // Change to Many-to-Many to allow a semester to belong to multiple degrees
    @JsonBackReference("semesters-degree")
    @ManyToMany
    @JoinTable(
            name = "academic_degree_semester",
            joinColumns = @JoinColumn(name = "semester_id"),
            inverseJoinColumns = @JoinColumn(name = "degree_id")
    )
    @JsonProperty("academic_degrees")
    private List<AcademicDegree> academicDegrees;

    @JsonProperty("start_date")
    private Date startDate;

    @JsonProperty("end_date")
    private Date endDate;

    @NonNull
    @Column(name = "archived", nullable = false)
    @JsonProperty("archived")
    private Character archived = 'N';
    @OneToMany(mappedBy = "semester")
    @JsonIgnore
    private List<CourseSemesterDegree> courseSemesterDegrees;

    public Semester(Long semesterId, String semesterName, Date startDate, Date endDate, @NonNull Character archived) {
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.archived = archived;
    }

}
