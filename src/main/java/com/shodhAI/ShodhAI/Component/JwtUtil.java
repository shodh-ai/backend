package com.shodhAI.ShodhAI.Component;

import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.StudentService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private String secretKeyString = "DASYWgfhMLL0np41rKFAGminD1zb5DlwDzE1WwnP8es=";

    private StudentService studentService;
    private ExceptionHandlingService exceptionHandling;
    private RoleService roleService;
    private EntityManager entityManager;
    private TokenBlacklist tokenBlacklist;

    @Autowired
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Autowired
    public void setExceptionHandlingService(ExceptionHandlingService exceptionHandling) {
        this.exceptionHandling = exceptionHandling;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setTokenBlacklist(TokenBlacklist tokenBlacklist) {
        this.tokenBlacklist = tokenBlacklist;
    }

    private Key secretKey;

    @PostConstruct
    public void init() {
        try {
            byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKeyString);
            if (secretKeyBytes.length * 8 < 256) {
                throw new IllegalArgumentException("Key length is less than 256 bits.");
            }

            this.secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
        } catch (Exception exception) {
            exceptionHandling.handleException(exception);
            throw new RuntimeException("Error generating JWT token", exception);
        }
    }

    public String generateToken(Long id, Long role, String ipAddress, String userAgent) {
        try {
            String uniqueTokenId = UUID.randomUUID().toString();

            boolean isMobile = isMobileDevice(userAgent);

            JwtBuilder jwtBuilder = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setId(uniqueTokenId)
                    .claim("id", id)
                    .claim("role", role)
                    .claim("userAgent", userAgent)
                    .claim("ipAddress", ipAddress)
                    .setIssuedAt(new Date())
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256);

            if (!isMobile) {
                jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 10))); // 10 hours
            }

            return jwtBuilder.compact();

        } catch (Exception e) {
            exceptionHandling.handleException(e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    private boolean isMobileDevice(String userAgent) {
        String devicePattern = "android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini";
        return userAgent != null && userAgent.toLowerCase().matches(".*(" + devicePattern + ").*");
    }

/*
    public String generateToken(Long id, Integer role, String ipAddress, String userAgent) {
        try {
            String uniqueTokenId = UUID.randomUUID().toString();

            return Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setId(uniqueTokenId)
                    .claim("id", id)
                    .claim("role", role)
                    .claim("ipAddress", ipAddress)
                    .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60  *30))
                   //.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            exceptionHandling.handleException(e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }*/

    private Key getSignInKey() {

        try {
            byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKeyString);
            this.secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
            return this.secretKey;
        } catch (Exception exception) {
            exceptionHandling.handleException(exception);
            throw new RuntimeException("Error generating JWT token", exception);
        }

    }

    public String extractUserAgent(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token is required");
            }

            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userAgent", String.class);

        }
/*        catch (SignatureException signatureException) {
            exceptionHandling.handleException(signatureException);
            throw new RuntimeException("Invalid JWT signature.");
        }*/ catch (Exception exception) {
            exceptionHandling.handleException(exception);
            throw new RuntimeException("Error extracting userAgent from JWT token", exception);
        }
    }

    public Long extractId(String token) {

        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token is required");
            }

            String userAgent = extractUserAgent(token);

            if (isTokenExpired(token, userAgent)) {
                throw new ExpiredJwtException(null, null, "Token is expired");

            }
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id", Long.class);
        } catch (ExpiredJwtException e) {
            logoutUser(token);
            throw e;
        } catch (Exception e) {
            exceptionHandling.handleException(e);
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public Date getExpiryTime(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token is required");
            }

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration();

        } catch (ExpiredJwtException e) {
            logoutUser(token);
            throw new ExpiredJwtException(null, null, "Token is expired");
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    @Transactional
    public Boolean validateToken(String token, String ipAddress, String userAgent) {

        try {

            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token is required");
            }
            if (isTokenExpired(token, userAgent)) {
                throw new IllegalArgumentException("Token is expired");
            }

            Long id = extractId(token);
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String tokenId = claims.getId();
            if (tokenBlacklist.isTokenBlacklisted(tokenId)) {
                return false;
            }
            Long roleId = extractRoleId(token);
            Student existingStudent = null;
//            Faculty existingFaculty = null;
//            Admin existingAdmin = null;
            if (roleService.findRoleNameById(roleId).equals(Constant.ROLE_USER)) {
                existingStudent = studentService.getStudentById(id);
                if (existingStudent == null) {
                    return false;
                }
            } else if (roleService.findRoleNameById(roleId).equals(Constant.ROLE_FACULTY)) {
//                existingFaculty = entityManager.find(Faculty.class, id);
//                if (existingFaculty == null)
//                    return false;
            }
//            else if (roleService.findRoleNameById(roleId).equals(Constant.roleAdmin) || roleService.findRoleNameById(roleId).equals(Constant.roleAdminServiceProvider)) {
//                existingAdmin = entityManager.find(Admin.class, id);
//                if (existingAdmin == null) {
//                    return false;
//                }
//            }

            String storedIpAddress = claims.get("ipAddress", String.class);

            return ipAddress.trim().equals(storedIpAddress != null ? storedIpAddress.trim() : "");
        } catch (ExpiredJwtException e) {
            logoutUser(token);
            return false;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            exceptionHandling.handleException(e);
            return false;
        } catch (Exception exception) {
            exceptionHandling.handleException(exception);
            return false;
        }
    }

    private boolean isTokenExpired(String token, String userAgent) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token is required");
            }

            boolean isMobile = isMobileDevice(userAgent);

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();

            if (isMobile && expiration == null) {
                return false; // Mobile token doesn't have expiration
            }

            return expiration != null && expiration.before(new Date());

        } catch (ExpiredJwtException e) {
            logoutUser(token);
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token is expired and cannot be used.");
        } catch (MalformedJwtException e) {
            exceptionHandling.handleException(e);
            throw new RuntimeException("Invalid JWT token", e);
        } catch (Exception e) {
            exceptionHandling.handleException(e);
            throw new RuntimeException("Error checking token expiration", e);
        }
    }

    public boolean logoutUser(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {

                throw new IllegalArgumentException("Token is required");

            }
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String uniqueTokenId = claims.getId();
            tokenBlacklist.blacklistToken(token, claims.getExpiration().getTime());
            return true;
        } catch (ExpiredJwtException e) {
//             tokenBlacklist.blacklistToken(token,claims.getExpiration().getTime());
            return true;
        } catch (Exception e) {
            exceptionHandling.handleException(e);
            return false;
        }
    }

    public Long extractRoleId(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token is required");
            }

            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", Long.class);

        }
//        catch (SignatureException signatureException) {
//            exceptionHandling.handleException(signatureException);
//            throw new RuntimeException("Invalid JWT signature.");
//        }
        catch (Exception exception) {
            exceptionHandling.handleException(exception);
            throw new RuntimeException("Error in JWT token", exception);
        }
    }

}
