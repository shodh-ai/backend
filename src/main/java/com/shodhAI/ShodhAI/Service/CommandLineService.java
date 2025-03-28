package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.AcademicDegree;
import com.shodhAI.ShodhAI.Entity.ContentType;
import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.DeliveryStatus;
import com.shodhAI.ShodhAI.Entity.DoubtLevel;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.NotificationType;
import com.shodhAI.ShodhAI.Entity.PriorityLevel;
import com.shodhAI.ShodhAI.Entity.QuestionType;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Semester;
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

        if (entityManager.createQuery("SELECT count(ad) FROM AcademicDegree ad", Long.class).getSingleResult() == 0) {
            entityManager.merge(new AcademicDegree(1L, "Master of Business Administration", "MBA", "Gyan Vihar University", 'N', new Date()));
            entityManager.merge(new AcademicDegree(2L, "Bachelor of Science", "BSc", "XYZ University", 'N', new Date()));
            entityManager.merge(new AcademicDegree(3L, "Master of Science", "MSc", "ABC University", 'N', new Date()));
            entityManager.merge(new AcademicDegree(4L, "Doctor of Philosophy", "PhD", "LMN Institute", 'N', new Date()));
        }

        if(entityManager.createQuery("SELECT count(s) FROM Semester s",Long.class).getSingleResult()==0)
        {
          entityManager.merge(new Semester(1L, "Semester 1", new Date(), new Date(), 'N'));
          entityManager.merge(new Semester(2L, "Semester 2", new Date(), new Date(), 'N'));
          entityManager.merge(new Semester(3L, "Semester 3", new Date(), new Date(), 'N'));
          entityManager.merge(new Semester(4L, "Semester 4", new Date(), new Date(), 'N'));
        }

        if (entityManager.createQuery("SELECT count(c) FROM Course c", Long.class).getSingleResult() == 0) {
            // MBA Courses
            entityManager.merge(new Course(1L, "Business Fundamentals", "Introduction to Business", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(2L, "Financial Management", "Corporate Finance & Accounting", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(3L, "Marketing Strategies", "Marketing & Brand Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(4L, "Organizational Behavior", "Leadership & Team Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(5L, "Operations Management", "Logistics & Supply Chain", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(6L, "Strategic Management", "Corporate Strategy & Business Policy", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(7L, "Human Resource Management", "Talent Acquisition & Performance Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(8L, "International Business", "Global Business & Trade", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(9L, "Entrepreneurship", "Business Startups & Innovation", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(10L, "Digital Marketing", "SEO, SEM & Social Media Marketing", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(11L, "Business Analytics", "Data Analysis for Business Decisions", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(12L, "E-Commerce Management", "Online Business Strategies", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(13L, "Leadership & Ethics", "Corporate Governance & Ethical Decision-Making", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(14L, "Supply Chain Management", "Inventory & Vendor Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(15L, "Corporate Law", "Legal Aspects of Business", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(16L, "Negotiation Skills", "Conflict Resolution & Bargaining", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(17L, "Project Management", "Planning, Execution & Risk Management", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(18L, "Customer Relationship Management", "Client Handling & Service Quality", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(19L, "Business Communication", "Effective Corporate Communication", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(20L, "Financial Risk Management", "Managing Risks in Investments & Business", 'N', new Date(), "6 months", new Date(), new Date()));

            // Other Courses (BSc, MSc, PhD)
            entityManager.merge(new Course(21L, "Physics 101", "Classical & Quantum Mechanics", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(22L, "Chemistry Basics", "Organic & Inorganic Chemistry", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(23L, "Advanced Data Science", "Machine Learning & AI", 'N', new Date(), "6 months", new Date(), new Date()));
            entityManager.merge(new Course(24L, "Doctoral Research Methods", "Quantitative & Qualitative Research", 'N', new Date(), "6 months", new Date(), new Date()));
        }


    }
}