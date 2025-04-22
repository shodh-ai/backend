package com.shodhAI.ShodhAI.service;

import com.shodhAI.ShodhAI.Component.Constant;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Service.RoleService;
import com.shodhAI.ShodhAI.Service.ExceptionHandlingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ExceptionHandlingService exceptionHandlingService;

    @Mock
    private TypedQuery<Role> roleTypedQuery;

    @InjectMocks
    private RoleService roleService;

    private Role validRole;
    private List<Role> roleList;

    @BeforeEach
    void setUp() {
        // Setup valid role
        validRole = new Role();
        validRole.setRoleId(1L);
        validRole.setRoleName("Female");

        // Setup role list
        Role role2 = new Role();
        role2.setRoleId(2L);
        role2.setRoleName("Male");

        roleList = Arrays.asList(validRole, role2);
    }

    @Test
    @DisplayName("Should get all roles successfully")
    void testGetAllRole() throws Exception {
        // Given
        when(entityManager.createQuery(eq(Constant.GET_ALL_ROLES), eq(Role.class)))
                .thenReturn(roleTypedQuery);
        when(roleTypedQuery.getResultList()).thenReturn(roleList);

        // When
        List<Role> result = roleService.getAllRole();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Female", result.get(0).getRoleName());
        assertEquals("Male", result.get(1).getRoleName());

        verify(entityManager).createQuery(eq(Constant.GET_ALL_ROLES), eq(Role.class));
        verify(roleTypedQuery).getResultList();
    }

    @Test
    @DisplayName("Should get role by ID successfully")
    void testGetRoleById() throws Exception {
        // Given
        when(entityManager.createQuery(anyString(), eq(Role.class)))
                .thenReturn(roleTypedQuery);
        when(roleTypedQuery.setParameter(eq("roleId"), eq(1L)))
                .thenReturn(roleTypedQuery);
        when(roleTypedQuery.getResultList())
                .thenReturn(Collections.singletonList(validRole));

        // When
        Role result = roleService.getRoleById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getRoleId());
        assertEquals("Female", result.getRoleName());

        verify(entityManager).createQuery(anyString(), eq(Role.class));
        verify(roleTypedQuery).setParameter("roleId", 1L);
    }

}
