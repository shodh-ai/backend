package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Gender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenderService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional(readOnly = true)
    public List<Gender> getAllGender() throws Exception {
        try {

            TypedQuery<Gender> query = entityManager.createQuery(Constant.GET_ALL_GENDERS, Gender.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Gender getGenderById(Long genderId) throws Exception {
        try {

            TypedQuery<Gender> query = entityManager.createQuery(Constant.GET_GENDER_BY_ID, Gender.class);
            query.setParameter("genderId", genderId);

            List<Gender> genderList = query.getResultList();
            if (genderList.isEmpty()) {
                return null;
            }
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
