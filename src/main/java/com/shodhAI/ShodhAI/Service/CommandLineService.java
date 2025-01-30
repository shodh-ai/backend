package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Gender;
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

    }
}