package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TreeDTOTest {

    private NodeDTO rootNode;
    private TreeDTO tree;

    @BeforeEach
    void setUp() {
        rootNode = new NodeDTO("condizione_radice");
        tree = new TreeDTO(rootNode, "tabella_test");
    }

    @Test
    void testConstructor() {
        assertSame(rootNode, tree.getRoot());
        assertEquals("tabella_test", tree.getTableName());
    }

}
