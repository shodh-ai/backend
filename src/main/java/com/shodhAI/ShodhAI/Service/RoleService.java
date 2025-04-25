package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExceptionHandlingService exceptionHandlingService;

    @Transactional(readOnly = true)
    public List<Role> getAllRole() throws Exception {
        try {

            TypedQuery<Role> query = entityManager.createQuery(Constant.GET_ALL_ROLES, Role.class);
            return query.getResultList();

        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Role getRoleByName(String roleName) throws Exception {
        try {
            TypedQuery<Role> query = entityManager.createQuery(Constant.GET_ROLE_BY_NAME, Role.class);
            query.setParameter("roleName", roleName);
            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }


    @Transactional(readOnly = true)
    public Role getRoleById(Long roleId) throws Exception {
        try {

            TypedQuery<Role> query = entityManager.createQuery(Constant.GET_ROLE_BY_ID, Role.class);
            query.setParameter("roleId", roleId);
            return query.getResultList().get(0);

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

    @Transactional(readOnly = true)
    public String findRoleNameById(Long roleId) throws Exception {
        try {
            String response = entityManager.createQuery(Constant.FETCH_ROLE_NAME_BY_ID, String.class)
                    .setParameter("roleId", roleId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (response == null)
                return "EMPTY";
            else return response;

        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            exceptionHandlingService.handleException(indexOutOfBoundsException);
            throw new IndexOutOfBoundsException(indexOutOfBoundsException.getMessage());
        } catch (Exception exception) {
            exceptionHandlingService.handleException(exception);
            throw new Exception(exception);
        }
    }

}
