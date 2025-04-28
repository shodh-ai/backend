package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Doubt;
import com.shodhAI.ShodhAI.Entity.Edge;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {
    private Long edgeId;
    private String source;
    private String target;
    private String type;
    private String description;
    private Doubt doubt;
    private Topic topic;

    @BeforeEach
    void setUp() {
        edgeId = 1L;
        source = "source";
        target = "target";
        type = "edge type";
        description = "description ";
        topic= new Topic();
        doubt= new Doubt();
    }

    @Test
    @DisplayName("testEdgeConstructor")
    void testEdgeConstructor(){
        Edge edgeByConstructor =  Edge.builder().id(edgeId).source(source).target(target).type(type).description(description).topic(topic).doubt(doubt).build();

        assertEquals(edgeId, edgeByConstructor.getId());
        assertEquals(source, edgeByConstructor.getSource());
        assertEquals(target, edgeByConstructor.getTarget());
        assertEquals(type, edgeByConstructor.getType());
        assertEquals(description, edgeByConstructor.getDescription());
        assertEquals(doubt, edgeByConstructor.getDoubt());
        assertEquals(topic, edgeByConstructor.getTopic());
    }

    @Test
    @DisplayName("testEdgeSettersAndGetters")
    void testEdgeSettersAndGetters(){
        Edge edge = getSource();
        assertEquals(edgeId, edge.getId());
        assertEquals(this.source, edge.getSource());
        assertEquals(target, edge.getTarget());
        assertEquals(type, edge.getType());
        assertEquals(topic, edge.getTopic());
        assertEquals(description, edge.getDescription());
        assertEquals(doubt, edge.getDoubt());
    }

    private @NotNull Edge getSource() {
        Edge edge = new Edge();
        edge.setId(edgeId);
        edge.setSource(this.source);
        edge.setTarget(target);
        edge.setType(type);
        edge.setDescription(description);
        edge.setDoubt(doubt);
        edge.setTopic(topic);
        return edge;
    }
}

