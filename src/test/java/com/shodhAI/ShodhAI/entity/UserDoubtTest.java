package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Doubt;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.UserDoubt;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class  UserDoubtTest {
    private Long userDoubtId;
    private Role role;
    private Doubt doubt;
    private Date askedAt;

    @BeforeEach
    void setUp() {
        userDoubtId = 1L;
        role = new Role();
        doubt = new Doubt();
        askedAt = new Date();
    }

    @Test
    @DisplayName("testUserDoubtConstructor")
    void testUserDoubtConstructor(){
        UserDoubt userDoubtByConstructor =  UserDoubt.builder().id(userDoubtId).role(role).doubt(doubt).askedAt(askedAt).build();

        assertEquals(userDoubtId, userDoubtByConstructor.getId());
        assertEquals(role, userDoubtByConstructor.getRole());
        assertEquals(doubt, userDoubtByConstructor.getDoubt());
        assertEquals(askedAt, userDoubtByConstructor.getAskedAt());
    }

    @Test
    @DisplayName("testUserDoubtSettersAndGetters")
    void testUserDoubtSettersAndGetters(){
        UserDoubt userDoubt = getUserDoubt();
        assertEquals(userDoubtId, userDoubt.getId());
        assertEquals(this.role, userDoubt.getRole());
        assertEquals(doubt, userDoubt.getDoubt());
        assertEquals(askedAt, userDoubt.getAskedAt());
    }

    private @NotNull UserDoubt getUserDoubt() {
        UserDoubt userDoubt = new UserDoubt();
        userDoubt.setId(userDoubtId);
        userDoubt.setRole(this.role);
        userDoubt.setDoubt(doubt);
        userDoubt.setAskedAt(askedAt);
        return userDoubt;
    }
}

