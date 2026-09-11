package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class NodeDTOTest {

    private NodeDTO internalNode;
    private NodeDTO leafNode;

    @BeforeEach
    void setUp() {
        internalNode = new NodeDTO("eta > 30");
        leafNode = new NodeDTO(42.5);
    }

    @Test
    void testInternalNodeCreation() {
        assertFalse(internalNode.isLeaf());
        assertEquals("eta > 30", internalNode.getSplitCondition());
        assertNull(internalNode.getPredictedValue());
        assertNotNull(internalNode.getChildren());
        assertTrue(internalNode.getChildren().isEmpty());
    }

    @Test
    void testLeafNodeCreation() {
        assertTrue(leafNode.isLeaf());
        assertEquals(42.5, leafNode.getPredictedValue());
        assertNull(leafNode.getSplitCondition());
        assertNotNull(leafNode.getChildren());
        assertTrue(leafNode.getChildren().isEmpty());
    }

    @Test
    void testAddChild() {
        NodeDTO child = new NodeDTO(10.0);
        internalNode.addChild(child);
        assertEquals(1, internalNode.getChildren().size());
        assertSame(child, internalNode.getChildren().get(0));
    }

    @Test
    void testMultipleChildren() {
        NodeDTO child1 = new NodeDTO("condizione A");
        NodeDTO child2 = new NodeDTO("condizione B");
        NodeDTO child3 = new NodeDTO(5.0);

        internalNode.addChild(child1);
        internalNode.addChild(child2);
        internalNode.addChild(child3);

        assertEquals(3, internalNode.getChildren().size());
        assertSame(child1, internalNode.getChildren().get(0));
        assertSame(child2, internalNode.getChildren().get(1));
        assertSame(child3, internalNode.getChildren().get(2));
    }

    @Test
    void testDefaultSelected() {
        assertTrue(internalNode.isSelected());
        assertTrue(leafNode.isSelected());
    }

    @Test
    void testToStringInternalNode() {
        assertEquals("eta > 30", internalNode.toString());
    }

    @Test
    void testToStringLeafNode() {
        assertEquals("Predizione: 42.5", leafNode.toString());
    }

    @Test
    public void testAddChildToLeafNode() {
        NodeDTO leaf = new NodeDTO(42.5);
        assertTrue(leaf.isLeaf(), "Il nodo creato con Double deve nascere come foglia");
        
        NodeDTO child = new NodeDTO("X <= 10");
        leaf.addChild(child);
        
        assertEquals(1, leaf.getChildren().size(), "La lista figli deve contenere il nodo aggiunto");
        assertEquals(child, leaf.getChildren().get(0));
    }
    

    @Test
    public void testToStringWithNullValues() {
        NodeDTO emptySplit = new NodeDTO((String) null);
        assertDoesNotThrow(() -> {
            String res = emptySplit.toString();
            assertNotNull(res);
        }, "toString non deve lanciare NPE con splitCondition null");

        NodeDTO emptyLeaf = new NodeDTO((Double) null);
        assertDoesNotThrow(() -> {
            String res = emptyLeaf.toString();
            assertNotNull(res);
        }, "toString non deve lanciare NPE con predictedValue null");
    }
    
}
