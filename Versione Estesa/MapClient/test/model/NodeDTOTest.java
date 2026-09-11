package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test unitari per la classe {@link NodeDTO}.
 * Verifica la creazione di nodi interni e foglie, i getter/setter,
 * la gestione dei figli e il comportamento di toString().
 */
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
    void testSetAndGetSplitCondition() {
        internalNode.setSplitCondition("reddito < 50000");
        assertEquals("reddito < 50000", internalNode.getSplitCondition());
    }

    @Test
    void testSetAndGetPredictedValue() {
        leafNode.setPredictedValue(99.99);
        assertEquals(99.99, leafNode.getPredictedValue());
    }

    @Test
    void testSetLeaf() {
        assertFalse(internalNode.isLeaf());
        internalNode.setLeaf(true);
        assertTrue(internalNode.isLeaf());

        assertTrue(leafNode.isLeaf());
        leafNode.setLeaf(false);
        assertFalse(leafNode.isLeaf());
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
    void testChildrenListIsMutable() {
        NodeDTO child = new NodeDTO(1.0);
        internalNode.addChild(child);

        // La lista ritornata da getChildren() e' la stessa referenza
        assertSame(internalNode.getChildren(), internalNode.getChildren());
        // Modifiche dirette sulla lista si riflettono
        internalNode.getChildren().add(new NodeDTO(2.0));
        assertEquals(2, internalNode.getChildren().size());
    }

    @Test
    void testDefaultSelected() {
        assertTrue(internalNode.isSelected());
        assertTrue(leafNode.isSelected());
    }

    @Test
    void testSetSelected() {
        internalNode.setSelected(false);
        assertFalse(internalNode.isSelected());

        leafNode.setSelected(false);
        assertFalse(leafNode.isSelected());

        leafNode.setSelected(true);
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
}
