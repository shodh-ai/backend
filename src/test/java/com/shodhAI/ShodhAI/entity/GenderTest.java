package com.shodhAI.ShodhAI.entity;
import com.shodhAI.ShodhAI.Entity.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenderTest {
    private Long genderId;
    private String genderName;
    private Character genderSymbol;
    private Date createdDate;
    private Date updatedDate;

    @BeforeEach
    void setUp() {
        genderId = 1L;
        genderName = "genderName";
        genderSymbol = 'N';
    }

    @Test
    @DisplayName("testGenderConstructor")
    void testGenderConstructor(){
        Gender genderByConstructor =  Gender.builder().genderId(genderId).genderName(genderName).genderSymbol(genderSymbol).build();

        assertEquals(genderId, genderByConstructor.getGenderId());
        assertEquals(genderName, genderByConstructor.getGenderName());
        assertEquals(genderSymbol, genderByConstructor.getGenderSymbol());
    }

    @Test
    @DisplayName("testGenderSettersAndGetters")
    void testGenderSettersAndGetters(){
        Gender gender = new Gender();
        gender.setGenderId(genderId);
        gender.setGenderName(genderName);
        gender.setGenderSymbol(genderSymbol);
        assertEquals(genderId, gender.getGenderId());
        assertEquals(genderName, gender.getGenderName());
        assertEquals(genderSymbol, gender.getGenderSymbol()
        );
    }
}



