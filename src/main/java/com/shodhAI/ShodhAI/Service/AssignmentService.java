package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Dto.AssignmentDto;
import com.shodhAI.ShodhAI.Dto.AssignmentStatisticsDto;
import com.shodhAI.ShodhAI.Dto.StudentCompletionDto;
import com.shodhAI.ShodhAI.Entity.Assignment;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.StudentAssignment;
import com.shodhAI.ShodhAI.Entity.Topic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    PriorityLevelService priorityLevelService;

    @Autowired
    TopicService topicService;

    public void validateAssignment(AssignmentDto assignmentDto) throws Exception {
        try {
            if (assignmentDto.getAssignmentName() == null || assignmentDto.getAssignmentName().isEmpty()) {
                throw new IllegalArgumentException("Assignment name cannot be null or empty");
            }
            assignmentDto.setAssignmentName(assignmentDto.getAssignmentName().trim());

            if (assignmentDto.getAssignmentDescription() != null) {
                if (assignmentDto.getAssignmentDescription().isEmpty() || assignmentDto.getAssignmentDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("Assignment Description cannot be empty");
                }
                assignmentDto.setAssignmentDescription(assignmentDto.getAssignmentDescription().trim());
            }

            if (assignmentDto.getTopicId() == null || assignmentDto.getTopicId() <= 0) {
                throw new IllegalArgumentException(("Topic Id cannot be null or <= 0"));
            }
            if (assignmentDto.getPriorityLevelId() == null || assignmentDto.getPriorityLevelId() <= 0) {
                throw new IllegalArgumentException(("Priority Level cannot be null or <= 0"));
            }

            // Dates
            if (assignmentDto.getActiveStartDate() != null && assignmentDto.getActiveEndDate() != null) {
                if (!assignmentDto.getActiveStartDate().before(assignmentDto.getActiveEndDate())) {
                    throw new IllegalArgumentException("Assignment Start date must be before of end date");
                }
            } else if (assignmentDto.getActiveStartDate() == null && assignmentDto.getActiveEndDate() != null) {
                throw new IllegalArgumentException("Assignment Start date cannot be null if Assignment End date is passed");
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Assignment saveAssignment(AssignmentDto assignmentDto) throws Exception {
        try {

            Topic topic = topicService.getTopicById(assignmentDto.getTopicId());
            PriorityLevel priorityLevel = priorityLevelService.getPriorityLevelById(assignmentDto.getPriorityLevelId());

            Assignment assignment = new Assignment();

            Date currentDate = new Date();

            assignment.setCreatedDate(currentDate);
            assignment.setUpdatedDate(currentDate);
            assignment.setAssignmentName(assignmentDto.getAssignmentName());
            assignment.setAssignmentDescription(assignmentDto.getAssignmentDescription());
            assignment.setActiveStartDate(assignmentDto.getActiveStartDate());
            assignment.setActiveEndDate(assignmentDto.getActiveEndDate());
            assignment.setPriorityLevel(priorityLevel);
            assignment.setTopic(topic);

            return entityManager.merge(assignment);

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    public Assignment getAssignmentById(Long assignmentId) throws Exception {
        try {

            TypedQuery<Assignment> query = entityManager.createQuery(Constant.GET_ASSIGNMENT_BY_ID, Assignment.class);
            query.setParameter("assignmentId", assignmentId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Assignment not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    public void validateAssignmentParameters(Long topicId) throws Exception {
        try {

            if (topicId == null || topicId <= 0) {
                throw new IllegalArgumentException(("Topic Id cannot be null or <= 0"));
            }

        } catch (IllegalArgumentException illegalArgumentException) {
            exceptionHandlingService.handleException(illegalArgumentException);
            throw new IllegalArgumentException(illegalArgumentException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public List<Assignment> filterAssignments(Long topicId) throws Exception {
        try {
            // Initialize the JPQL query
            StringBuilder jpql = new StringBuilder("SELECT a FROM Assignment a WHERE 1=1 ");

            Topic topic = null;
            if (topicId != null) {
                topic = topicService.getTopicById(topicId);
                jpql.append("AND a.topic = :topic ");
            }

            // Create the query with the final JPQL string
            TypedQuery<Assignment> query = entityManager.createQuery(jpql.toString(), Assignment.class);

            if (query != null) {
                query.setParameter("topic", topic);
            }

            // Execute and return the result
            return query.getResultList();

        } catch (PersistenceException persistenceException) {
            exceptionHandlingService.handleException(persistenceException);
            throw new PersistenceException(persistenceException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional
    public Assignment assignToAllStudents(Long assignmentId, Long facultyId) {
        // Get the assignment
        Assignment assignment = entityManager.find(Assignment.class, assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        // Get the faculty
        Faculty faculty = entityManager.find(Faculty.class, facultyId);
        if (faculty == null) {
            throw new IllegalArgumentException("Faculty not found with ID: " + facultyId);
        }

        // Verify faculty has access to this assignment
        boolean hasAccess = entityManager.createQuery(
                        "SELECT COUNT(a) FROM Assignment a " +
                                "JOIN a.topic t " +
                                "JOIN t.course c " +
                                "JOIN c.facultyMembers f " +
                                "WHERE a.assignmentId = :assignmentId " +
                                "AND f.id = :facultyId", Long.class)
                .setParameter("assignmentId", assignmentId)
                .setParameter("facultyId", facultyId)
                .getSingleResult() > 0;

        if (!hasAccess) {
            throw new IllegalArgumentException("Faculty does not have access to this assignment");
        }

        // Get all students associated with the faculty
        List<Student> students = entityManager.createQuery(
                        "SELECT s FROM Student s " +
                                "JOIN s.facultyMembers f " +
                                "WHERE f.id = :facultyId " +
                                "AND s.archived = 'N'", Student.class)
                .setParameter("facultyId", facultyId)
                .getResultList();

        if (students.isEmpty()) {
            return null;
        }

        // Get student IDs
        List<Long> studentIds = students.stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        // Check for existing assignments to avoid duplicates
        List<StudentAssignment> existingAssignments = entityManager.createQuery(
                        "SELECT sa FROM StudentAssignment sa " +
                                "WHERE sa.assignment.assignmentId = :assignmentId " +
                                "AND sa.student.id IN :studentIds", StudentAssignment.class)
                .setParameter("assignmentId", assignmentId)
                .setParameter("studentIds", studentIds)
                .getResultList();

        // Create a map of student IDs to existing assignments
        Map<Long, StudentAssignment> existingMap = existingAssignments.stream()
                .collect(Collectors.toMap(sa -> sa.getStudent().getId(), sa -> sa));

        // Create new StudentAssignment entities
        int assignedCount = 0;
        Date now = new Date();

        for (Student student : students) {
            // Skip if assignment already exists for this student
            if (existingMap.containsKey(student.getId())) {
                continue;
            }

            StudentAssignment studentAssignment = new StudentAssignment();
            studentAssignment.setStudent(student);
            studentAssignment.setAssignment(assignment);
            studentAssignment.setCompletionStatus(false);
            studentAssignment.setCreatedDate(now);
            studentAssignment.setUpdatedDate(now);

            entityManager.persist(studentAssignment);
            assignedCount++;
        }

        entityManager.flush();
        return assignment;
    }

    public AssignmentStatisticsDto getAssignmentCompletionStatistics(Long assignmentId, Long facultyId) {
        // Get the assignment
        Assignment assignment = entityManager.find(Assignment.class, assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        // Get the faculty
        Faculty faculty = entityManager.find(Faculty.class, facultyId);
        if (faculty == null) {
            throw new IllegalArgumentException("Faculty not found with ID: " + facultyId);
        }

        // Get all students associated with the faculty
        TypedQuery<Student> studentQuery = entityManager.createQuery(
                "SELECT s FROM Student s JOIN s.facultyMembers f WHERE f.id = :facultyId AND s.archived = 'N'",
                Student.class);
        studentQuery.setParameter("facultyId", facultyId);
        List<Student> students = studentQuery.getResultList();

        // Get student assignment data
        TypedQuery<StudentAssignment> assignmentQuery = entityManager.createQuery(
                "SELECT sa FROM StudentAssignment sa WHERE sa.assignment.assignmentId = :assignmentId " +
                        "AND sa.student.id IN :studentIds",
                StudentAssignment.class);
        assignmentQuery.setParameter("assignmentId", assignmentId);
        assignmentQuery.setParameter("studentIds", students.stream().map(Student::getId).collect(Collectors.toList()));
        List<StudentAssignment> studentAssignments = assignmentQuery.getResultList();

        // Prepare statistics
        int totalStudents = students.size();
        int completedCount = (int) studentAssignments.stream()
                .filter(sa -> sa.getCompletionStatus() != null && sa.getCompletionStatus())
                .count();

        double completionPercentage = totalStudents > 0 ?
                (completedCount * 100.0) / totalStudents : 0.0;

        // Student completion list
        List<StudentCompletionDto> studentCompletionList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Track completion by date
        Map<String, Integer> completionByDate = new HashMap<>();

        for (Student student : students) {
            StudentAssignment studentAssignment = studentAssignments.stream()
                    .filter(sa -> sa.getStudent().getId().equals(student.getId()))
                    .findFirst()
                    .orElse(null);

            Boolean completed = false;
            Double score = null;
            String submissionDate = null;

            if (studentAssignment != null) {
                completed = studentAssignment.getCompletionStatus() != null ?
                        studentAssignment.getCompletionStatus() : false;
                score = studentAssignment.getScore();

                if (studentAssignment.getSubmissionDate() != null) {
                    submissionDate = dateFormat.format(studentAssignment.getSubmissionDate());

                    // Count completions by date
                    if (completed) {
                        completionByDate.put(
                                submissionDate,
                                completionByDate.getOrDefault(submissionDate, 0) + 1
                        );
                    }
                }
            }

            studentCompletionList.add(new StudentCompletionDto(
                    student.getId(),
                    student.getFirstName() + " " + student.getLastName(),
                    completed,
                    score,
                    submissionDate
            ));
        }

        // Create and return the statistics DTO
        AssignmentStatisticsDto statisticsDTO = new AssignmentStatisticsDto();
        statisticsDTO.setAssignmentId(assignmentId);
        statisticsDTO.setAssignmentName(assignment.getAssignmentName());
        statisticsDTO.setTotalStudents(totalStudents);
        statisticsDTO.setCompletedCount(completedCount);
        statisticsDTO.setCompletionPercentage(Math.round(completionPercentage * 100) / 100.0); // Round to 2 decimal places
        statisticsDTO.setStudentCompletionList(studentCompletionList);
        statisticsDTO.setCompletionByDate(completionByDate);

        return statisticsDTO;
    }
}
