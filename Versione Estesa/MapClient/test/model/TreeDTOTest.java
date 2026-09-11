package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test unitari per la classe {@link TreeDTO}.
 * Verifica la costruzione, i getter/setter di root e tableName.
 */
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

    @Test
    void testSetAndGetRoot() {
        NodeDTO newRoot = new NodeDTO(100.0);
        tree.setRoot(newRoot);
        assertSame(newRoot, tree.getRoot());
    }

    @Test
    void testSetAndGetTableName() {
        tree.setTableName("nuova_tabella");
        assertEquals("nuova_tabella", tree.getTableName());
    }

    @Test
    void testRootCanBeReplaced() {
        NodeDTO firstRoot = tree.getRoot();
        NodeDTO secondRoot = new NodeDTO("nuova_condizione");
        tree.setRoot(secondRoot);

        assertNotSame(firstRoot, tree.getRoot());
        assertSame(secondRoot, tree.getRoot());
    }

    @Test
    void testTableNameCanBeChanged() {
        assertEquals("tabella_test", tree.getTableName());
        tree.setTableName("altro_nome");
        assertEquals("altro_nome", tree.getTableName());
    }
}
