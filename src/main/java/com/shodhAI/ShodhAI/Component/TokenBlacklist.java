package com.shodhAI.ShodhAI.Component;

import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Service.RoleService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Token blacklist.
 */

@Service
@Slf4j
public class TokenBlacklist {

    @Autowired
    @Lazy
    private JwtUtil jwtUtil;

    @Autowired
    EntityManager entityManager;

    @Autowired
    RoleService roleService;
    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    /**
     * Blacklist token.
     *
     * @param token          the token
     * @param expirationTime the expiration time
     */
    @Transactional
    public void blacklistToken(String token,Long exp) {
        try {
            blacklistedTokens.put(token, exp);
            Long id = jwtUtil.extractId(token);
            Long roleId = jwtUtil.extractRoleId(token);
            if(roleService.findRoleNameById(roleId).equals(Constant.ROLE_USER))
            {
                Student existingStudent = entityManager.find(Student.class,id);
                if (existingStudent != null) {
                    existingStudent.setToken(null);
                    entityManager.merge(existingStudent);
                } else {
                    throw new RuntimeException("Student not found for the given token");
                }
            }

            /*if(roleService.findRoleNameById(roleId).equals(Constant.serviceProviderRoles))
            {
                Teacher existingTeacher = em.find(ServiceProviderEntity.class,id);
                if (existintServiceProviderEntity != null) {
                    existintServiceProviderEntity.setToken(null);
                    em.merge(existintServiceProviderEntity);
                } else {
                    throw new RuntimeException("SP not found for the given token");
                }
            }*/
        } catch (ExpiredJwtException expiredJwtException) {
            throw expiredJwtException;
        } catch (Exception e) {
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    /**
     * Is token blacklisted boolean.
     *
     * @param token the token
     * @return the boolean
     */
    public boolean isTokenBlacklisted(String token) {
        Long expirationTime = blacklistedTokens.get(token);

        if (expirationTime != null && expirationTime > System.currentTimeMillis()) {
            return true;
        } else {
            blacklistedTokens.remove(token);
            return false;
        }
    }


    /**
     * Clean expired tokens.
     */
//    @Scheduled(fixedRate = 60000) // 1 minutes interval
    @Scheduled(fixedRate = 36000000)  // 10 hour interval
    public void cleanExpiredTokens() {
        long currentTime = System.currentTimeMillis();

        Iterator<Map.Entry<String, Long>> iterator = blacklistedTokens.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            long expirationTime = entry.getValue();

            if (expirationTime < currentTime) {
                iterator.remove();
            }
        }

        log.info("Expired tokens cleaned up from blacklist");
    }
}

