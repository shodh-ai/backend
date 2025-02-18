package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Content;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.StudentSimulationProgress;
import com.shodhAI.ShodhAI.Entity.Topic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentSimulationProgressService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ExceptionHandlingService exceptionHandlingService;

    @Transactional
    public StudentSimulationProgress updateSimulationProgress(Long studentId, Long topicId, Double timestamp) throws Exception {
        try {

            StudentSimulationProgress studentSimulationProgress = getStudentSimulationProgress(studentId, topicId);
            if(studentSimulationProgress == null) {
                studentSimulationProgress = new StudentSimulationProgress();
                studentSimulationProgress.setStudent(studentService.getStudentById(studentId));
                studentSimulationProgress.setTopic(topicService.getTopicById(topicId));
                studentSimulationProgress.setTimestamp(timestamp);

            } else {
                studentSimulationProgress.setTimestamp(timestamp);
            }
            return entityManager.merge(studentSimulationProgress);

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }


    public StudentSimulationProgress getStudentSimulationProgress(Long studentId, Long topicId) throws Exception {
        try {

            Student student = studentService.getStudentById(studentId);
            Topic topic = topicService.getTopicById(topicId);

            TypedQuery<StudentSimulationProgress> query = entityManager.createQuery(Constant.GET_STUDENT_SIMULATION_PROGRESS, StudentSimulationProgress.class);
            query.setParameter("topic", topic);
            query.setParameter("student", student);

            List<StudentSimulationProgress> studentSimulationProgressList = query.getResultList();

            if(!studentSimulationProgressList.isEmpty()) {
                return studentSimulationProgressList.get(0);
            }
            return null;

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException("Student Simulation Progress not found with given Id");
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
