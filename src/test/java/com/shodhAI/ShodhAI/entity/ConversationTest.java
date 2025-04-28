package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Conversation;
import com.shodhAI.ShodhAI.Entity.Role;
import com.shodhAI.ShodhAI.Entity.Session;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversationTest {
    private Long conversationId;
    private String userDialogue;
    private String assistantDialogue;
    private Date userDialogueTimestamp;
    private Date assistantDialogueTimestamp;
    private Session session;
    private Role role;

    @BeforeEach
    void setUp() {
        conversationId = 1L;
        userDialogue = "conversationName";
        assistantDialogue = "programName";
        session = new Session();
        userDialogueTimestamp = new Date();
        assistantDialogueTimestamp = new Date();
        role = new Role();
    }

    @Test
    @DisplayName("testConversationConstructor")
    void testConversationConstructor(){
        Conversation conversationByConstructor =  Conversation.builder().id(conversationId).userDialogue(userDialogue).assistantDialogue(assistantDialogue).userDialogueTimestamp(userDialogueTimestamp).assistantDialogueTimestamp(assistantDialogueTimestamp).session(session).userRole(role).build();

        assertEquals(conversationId, conversationByConstructor.getId());
        assertEquals(userDialogue, conversationByConstructor.getUserDialogue());
        assertEquals(assistantDialogue, conversationByConstructor.getAssistantDialogue());
        assertEquals(userDialogueTimestamp, conversationByConstructor.getUserDialogueTimestamp());
        assertEquals(assistantDialogueTimestamp, conversationByConstructor.getAssistantDialogueTimestamp());
        assertEquals(session, conversationByConstructor.getSession());
        assertEquals(role, conversationByConstructor.getUserRole());
    }

    @Test
    @DisplayName("testConversationSettersAndGetters")
    void testConversationSettersAndGetters(){
        Conversation conversation = getConversation();
        assertEquals(conversationId, conversation.getId());
        assertEquals(userDialogue, conversation.getUserDialogue());
        assertEquals(assistantDialogue, conversation.getAssistantDialogue());
        assertEquals(role, conversation.getUserRole());
        assertEquals(session, conversation.getSession());
        assertEquals(userDialogueTimestamp, conversation.getUserDialogueTimestamp());
        assertEquals(assistantDialogueTimestamp, conversation.getAssistantDialogueTimestamp());
    }

    private @NotNull Conversation getConversation() {
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setUserDialogue(userDialogue);
        conversation.setAssistantDialogue(assistantDialogue);
        conversation.setUserDialogueTimestamp(userDialogueTimestamp);
        conversation.setAssistantDialogueTimestamp(assistantDialogueTimestamp);
        conversation.setUserRole(role);
        conversation.setSession(session);
        return conversation;
    }
}


