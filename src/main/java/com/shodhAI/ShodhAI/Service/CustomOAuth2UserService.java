package com.shodhAI.ShodhAI.Service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Component.GoogleUserInfo;
import com.shodhAI.ShodhAI.Dto.FacultyDto;
import com.shodhAI.ShodhAI.Dto.StudentDto;
import com.shodhAI.ShodhAI.Entity.Faculty;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Student;
import com.shodhAI.ShodhAI.Entity.UserPrincipal;
import com.shodhAI.ShodhAI.exceptions.OAuth2AuthenticationProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final StudentService studentService;
    private final EntityManager entityManager;
    private final FacultyService facultyService;
    private final ExceptionHandlingService exceptionHandlingService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomOAuth2UserService(@Lazy StudentService studentService,
                                   EntityManager entityManager,
                                   @Lazy FacultyService facultyService,
                                   ExceptionHandlingService exceptionHandlingService,
                                   RoleService roleService,
                                   PasswordEncoder passwordEncoder) {
        this.studentService = studentService;
        this.entityManager = entityManager;
        this.facultyService = facultyService;
        this.exceptionHandlingService = exceptionHandlingService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    @Transactional
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        GoogleUserInfo userInfo = new GoogleUserInfo(attributes);
        String email = userInfo.getEmail();

        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        Long selectedRoleId = (Long) session.getAttribute("selected_role_id");

        if (selectedRoleId == null) {
            try {
                Role userRole = roleService.getRoleByName(Constant.ROLE_USER);
                selectedRoleId = userRole.getRoleId();
            } catch (Exception e) {
                selectedRoleId = 4L;
            }
        }

        String roleName = Constant.ROLE_USER;
        try {
            Role role = roleService.getRoleById(selectedRoleId);
            roleName = role.getRoleName();
        } catch (Exception ignored) {}

        if (Constant.ROLE_USER.equals(roleName)) {
            List<Student> students = studentService.filterStudents(null, null, email);
            if (students.isEmpty()) {
                Student student = registerNewStudent(email, userInfo, selectedRoleId);
                return new UserPrincipal(student.getId(), email, null, attributes, Constant.ROLE_USER);
            } else {
                Student student = students.get(0);
                if (!"GOOGLE".equals(student.getAuthProvider())) {
                    updateStudentAuthProvider(student, userInfo.getImageUrl());
                }
                return new UserPrincipal(student.getId(), email, null, attributes, Constant.ROLE_USER);
            }
        } else {
            List<Faculty> faculties = facultyService.filterFaculties(null, null, email);
            if (faculties.isEmpty()) {
                Faculty faculty = registerNewFaculty(email, userInfo, selectedRoleId);
                return new UserPrincipal(faculty.getId(), email, null, attributes, Constant.ROLE_FACULTY);
            } else {
                Faculty faculty = faculties.get(0);
                if (!"GOOGLE".equals(faculty.getAuthProvider())) {
                    updateFacultyAuthProvider(faculty, userInfo.getImageUrl());
                }
                return new UserPrincipal(faculty.getId(), email, null, attributes, Constant.ROLE_FACULTY);
            }
        }
    }

    @Transactional
    private Student registerNewStudent(String email, GoogleUserInfo userInfo, Long roleId) {
        StudentDto studentDto = new StudentDto();
        studentDto.setPersonalEmail(email);
        studentDto.setUserName(email.split("@")[0]);

        if (userInfo.getName() != null) {
            String[] nameParts = userInfo.getName().split(" ");
            if (nameParts.length > 0) {
                studentDto.setFirstName(nameParts[0]);
                if (nameParts.length > 1) {
                    studentDto.setLastName(nameParts[nameParts.length - 1]);
                }
            }
        }

        if (userInfo.getImageUrl() != null) {
            studentDto.setProfilePictureUrl(userInfo.getImageUrl());
        }

        String randomPassword = UUID.randomUUID().toString();
        studentDto.setPassword(passwordEncoder.encode(randomPassword));
        studentDto.setGenderId(1L);

        try {
            Student student = studentService.saveStudent(studentDto, "", 'N');
            student.setAuthProvider("GOOGLE");
            return entityManager.merge(student);
        } catch (Exception e) {
            throw new OAuth2AuthenticationProcessingException("Error registering new student: " + e.getMessage());
        }
    }

    @Transactional
    private Faculty registerNewFaculty(String email, GoogleUserInfo userInfo, Long roleId) {
        FacultyDto facultyDto = new FacultyDto();
        facultyDto.setPersonalEmail(email);
        facultyDto.setUserName(email.split("@")[0]);

        if (userInfo.getName() != null) {
            String[] nameParts = userInfo.getName().split(" ");
            if (nameParts.length > 0) {
                facultyDto.setFirstName(nameParts[0]);
                if (nameParts.length > 1) {
                    facultyDto.setLastName(nameParts[nameParts.length - 1]);
                }
            }
        }

        if (userInfo.getImageUrl() != null) {
            facultyDto.setProfilePictureUrl(userInfo.getImageUrl());
        }

        String randomPassword = UUID.randomUUID().toString();
        facultyDto.setPassword(passwordEncoder.encode(randomPassword));
        facultyDto.setGenderId(1L);

        try {
            Faculty faculty = facultyService.saveFaculty(facultyDto, "", 'N');
            faculty.setAuthProvider("GOOGLE");
            return entityManager.merge(faculty);
        } catch (Exception e) {
            throw new OAuth2AuthenticationProcessingException("Error registering new faculty: " + e.getMessage());
        }
    }

    @Transactional
    public void updateStudentAuthProvider(Student student, String imageUrl) {
        student.setAuthProvider("GOOGLE");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            student.setProfilePictureUrl(imageUrl);
        }
        entityManager.merge(student);
    }

    @Transactional
    public void updateFacultyAuthProvider(Faculty faculty, String imageUrl) {
        faculty.setAuthProvider("GOOGLE");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            faculty.setProfilePictureUrl(imageUrl);
        }
        entityManager.merge(faculty);
    }
}
