package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.TopicType;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class CommandLineService implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (entityManager.createQuery("SELECT count(g) FROM Gender g", Long.class).getSingleResult() == 0) {
            entityManager.merge(new Gender(1L, 'M', "Male"));
            entityManager.merge(new Gender(2L, 'F', "Female"));
            entityManager.merge(new Gender(3L, 'O', "Others"));
        }

        if (entityManager.createQuery("SELECT count(r) FROM Role r", Long.class).getSingleResult() == 0) {
            Date currentDate = new Date();

            entityManager.merge(new Role(1L, currentDate, "SUPER_ADMIN", currentDate));
            entityManager.merge(new Role(2L, currentDate, "ADMIN", currentDate));
            entityManager.merge(new Role(3L, currentDate, "FACULTY", currentDate));
            entityManager.merge(new Role(4L, currentDate, "STUDENT", currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(f) FROM FileType f", Long.class).getSingleResult() == 0) {
            entityManager.merge(new FileType(1L, "PNG"));
            entityManager.merge(new FileType(2L, "JPG"));
            entityManager.merge(new FileType(3L, "PDF"));
            entityManager.merge(new FileType(4L, "JPEG"));
            entityManager.merge(new FileType(5L, "MP4"));
            entityManager.merge(new FileType(6L, "TXT"));
        }

        if (entityManager.createQuery("SELECT COUNT(t) FROM TopicType t", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new TopicType(1L, "TEACHING", 'N', currentDate, currentDate));
            entityManager.merge(new TopicType(2L, "ASSIGNMENT", 'N', currentDate, currentDate));
            entityManager.merge(new TopicType(3L, "ASSESSMENT", 'N', currentDate, currentDate));
            entityManager.merge(new TopicType(4L, "SIMULATION", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(c) FROM ContentType c", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new ContentType(1L, "TEACHING", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(2L, "PRACTICE_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(3L, "ASSIGNMENT_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(4L, "SIMULATION", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(c) FROM QuestionType c", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new QuestionType(1L, "PRACTICE_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new QuestionType(2L, "EXAMPLE_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new QuestionType(3L, "TESTING_QUESTION", 'N', currentDate, currentDate));
            entityManager.merge(new QuestionType(4L, "QUIZ", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(d) FROM DoubtLevel d", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new DoubtLevel(1L, "BASIC", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(2L, "MEDIATE", 'N', currentDate, currentDate));
            entityManager.merge(new ContentType(3L, "ADVANCED", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT COUNT(p) FROM PriorityLevel p", Long.class).getSingleResult() == 0) {

            Date currentDate = new Date();

            entityManager.merge(new PriorityLevel(1L, "HIGH", 'N', currentDate, currentDate));
            entityManager.merge(new PriorityLevel(2L, "MEDIUM", 'N', currentDate, currentDate));
            entityManager.merge(new PriorityLevel(3L, "LOW+", 'N', currentDate, currentDate));
        }

        if (entityManager.createQuery("SELECT count(nt) FROM NotificationType nt", Long.class).getSingleResult() == 0) {
            entityManager.merge(new NotificationType(1L, "EMAIL", "Email Notification", null));
            entityManager.merge(new NotificationType(2L, "SMS", "SMS Notification", null));
            entityManager.merge(new NotificationType(3L, "APP", "In-App Notification", null));
            entityManager.merge(new NotificationType(4L, "ALL", "All Notification Channels", null));
        }

        if (entityManager.createQuery("SELECT count(ds) FROM DeliveryStatus ds", Long.class).getSingleResult() == 0) {
            entityManager.merge(new DeliveryStatus(1L, "PENDING", "Notification is pending delivery", null));
            entityManager.merge(new DeliveryStatus(2L, "DELIVERED", "Notification has been delivered", null));
            entityManager.merge(new DeliveryStatus(3L, "FAILED", "Notification delivery failed", null));
        }

    }
}