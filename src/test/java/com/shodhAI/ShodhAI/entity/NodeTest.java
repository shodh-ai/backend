package com.shodhAI.ShodhAI.entity;

import com.shodhAI.ShodhAI.Entity.Doubt;
import com.shodhAI.ShodhAI.Entity.Node;
import com.shodhAI.ShodhAI.Entity.FileType;
import com.shodhAI.ShodhAI.Entity.Topic;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTest {
    private Long nodeId;
    private String name;
    private String type;
    private String columns;
    private String attributes;
    private String document;
    private List<String> properties;
    private Doubt doubt;
    private Topic topic;

    @BeforeEach
    void setUp() {
        nodeId = 1L;
        name = "node Name";
        type = "node type";
        columns = "columns";
        attributes ="attributes ";
        document = "documents";
        topic= new Topic();
    }

    @Test
    @DisplayName("testNodeConstructor")
    void testNodeConstructor(){
        Node nodeByConstructor =  Node.builder().id(nodeId).name(name).type(type).columns(columns).attributes(attributes).document(document).topic(topic).build();

        assertEquals(nodeId, nodeByConstructor.getId());
        assertEquals(name, nodeByConstructor.getName());
        assertEquals(type, nodeByConstructor.getType());
        assertEquals(columns, nodeByConstructor.getColumns());
        assertEquals(attributes, nodeByConstructor.getAttributes());
        assertEquals(document, nodeByConstructor.getDocument());
        assertEquals(topic, nodeByConstructor.getTopic());
    }

    @Test
    @DisplayName("testNodeSettersAndGetters")
    void testNodeSettersAndGetters(){
        Node node = getNode();
        assertEquals(nodeId, node.getId());
        assertEquals(this.name, node.getName());
        assertEquals(type, node.getType());
        assertEquals(columns, node.getColumns());
        assertEquals(topic, node.getTopic());
        assertEquals(attributes, node.getAttributes());
        assertEquals(document, node.getDocument());
    }

    private @NotNull Node getNode() {
        Node node = new Node();
        node.setId(nodeId);
        node.setName(this.name);
        node.setType(type);
        node.setColumns(columns);
        node.setAttributes(attributes);
        node.setDocument(document);
        node.setTopic(topic);
        return node;
    }
}

