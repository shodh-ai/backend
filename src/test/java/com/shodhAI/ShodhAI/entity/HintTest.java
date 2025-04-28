package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Course;
import com.shodhAI.ShodhAI.Entity.Hint;
import com.shodhAI.ShodhAI.Entity.Notification;
import com.shodhAI.ShodhAI.Entity.Student;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HintTest {
    private String level;
    private String text;


    @BeforeEach
    void setUp() {
        level = "level";
        text = "hint text";
    }

    @Test
    @DisplayName("testHintConstructor")
    void testHintConstructor(){
        Hint hintByConstructor =  Hint.builder().level(level).text(text).build();

        assertEquals(level, hintByConstructor.getLevel());
        assertEquals(text, hintByConstructor.getText());
    }

    @Test
    @DisplayName("testHintSettersAndGetters")
    void testHintSettersAndGetters(){
        Hint hint = getHint();
        assertEquals(level, hint.getLevel());
        assertEquals(text, hint.getText());
    }

    private @NotNull Hint getHint() {
        Hint hint = new Hint();
        hint.setLevel(level);
        hint.setText(text);
        return hint;
    }
}


