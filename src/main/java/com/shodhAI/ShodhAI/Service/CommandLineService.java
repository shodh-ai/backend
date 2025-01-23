package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Entity.Gender;
import com.shodhAI.ShodhAI.Entity.Role;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Component
public class CommandLineService implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (entityManager.createQuery("SELECT count(z) FROM Gender z", Long.class).getSingleResult() == 0) {
            entityManager.merge(new Gender(1L, 'M', "Male"));
            entityManager.merge(new Gender(2L, 'F', "Female"));
            entityManager.merge(new Gender(3L, 'O', "Others"));
        }

        if(entityManager.createQuery("SELECT count(r) FROM Role r", Long.class).getSingleResult() == 0) {
            Date currentDate = new Date();

            entityManager.merge(new Role(1L, currentDate, "SUPER_ADMIN", currentDate));
            entityManager.merge(new Role(2L, currentDate, "ADMIN", currentDate));
            entityManager.merge(new Role(3L, currentDate, "FACULTY", currentDate));
            entityManager.merge(new Role(4L, currentDate, "STUDENT", currentDate));
        }

    }
}