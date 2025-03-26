package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.ClarificationStatus;
import com.shodhAI.ShodhAI.Entity.ConceptClarification;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ConceptClarificationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public ConceptClarification requestConceptClarification(Long studentId, String term, Long courseId) {
        Student student = entityManager.find(Student.class, studentId);
        if (student == null) {
            throw new RuntimeException("Student not found");
        }
        // Optional: Fetch course if courseId is provided
        Course course = courseId != null ? entityManager.find(Course.class, courseId) : null;

        // Create concept clarification request
        ConceptClarification clarification = new ConceptClarification();
        clarification.setStudent(student);
        clarification.setTerm(term);
        clarification.setCourse(course);
        clarification.setCreatedDate(new Date());

        ClarificationStatus clarificationStatus= entityManager.find(ClarificationStatus.class,1L);
        if(clarificationStatus==null)
        {
            throw new IllegalArgumentException("There is no status present in database to update the status of clarification");
        }
        clarification.setClarificationStatus(clarificationStatus);
        entityManager.persist(clarification);
        return clarification;
    }

    @Transactional
    public ConceptClarification getConceptClarification(Long clarificationId) {
        ConceptClarification clarification = entityManager.find(ConceptClarification.class, clarificationId);
        if (clarification == null) {
            throw new IllegalArgumentException("Concept clarification not found");
        }
        return clarification;
    }
}