package com.shodhAI.ShodhAI.entity;
import com.shodhAI.ShodhAI.Entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {
    private Long roleId;
    private String roleName;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        roleId = 1L;
        roleName = "roleName";
        createdDate = new Date();
        updatedDate= new Date();
    }

    @Test
    @DisplayName("testRoleConstructor")
    void testRoleConstructor(){
        Role roleByConstructor =  Role.builder().roleId(roleId).roleName(roleName).createdDate(createdDate).updatedDate(updatedDate).build();

        assertEquals(roleId, roleByConstructor.getRoleId());
        assertEquals(roleName, roleByConstructor.getRoleName());
        assertEquals(createdDate, roleByConstructor.getCreatedDate());
        assertEquals(updatedDate, roleByConstructor.getUpdatedDate());
    }

    @Test
    @DisplayName("testRoleSettersAndGetters")
    void testRoleSettersAndGetters(){
        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        role.setCreatedDate(createdDate);
        role.setUpdatedDate(updatedDate);
        assertEquals(roleId, role.getRoleId());
        assertEquals(roleName, role.getRoleName());
        assertEquals(updatedDate, role.getUpdatedDate());
        assertEquals(createdDate, role.getCreatedDate());
    }
}



