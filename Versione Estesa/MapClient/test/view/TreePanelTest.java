package view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.NodeDTO;
import model.TreeDTO;

import javax.swing.SwingUtilities;
import java.lang.reflect.Field;

class TreePanelTest {

    private TreePanel panel;
    private Field treeField;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel = new TreePanel());
        treeField = TreePanel.class.getDeclaredField("currentTree");
        treeField.setAccessible(true);
    }

    @Test
    void testDefaultState() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals(java.awt.Color.WHITE, panel.getBackground());
        });
        assertNull(treeField.get(panel));
    }

    @Test
    void testUpdateTree() throws Exception {
        NodeDTO root = new NodeDTO("eta > 30");
        TreeDTO tree = new TreeDTO(root, "tabella_test");
        SwingUtilities.invokeAndWait(() -> panel.updateTree(tree));
        assertSame(tree, treeField.get(panel));
    }

    @Test
    void testResetTree() throws Exception {
        NodeDTO root = new NodeDTO("condizione");
        SwingUtilities.invokeAndWait(() -> {
            panel.updateTree(new TreeDTO(root, "tab"));
            panel.resetTree();
        });
        assertNull(treeField.get(panel));
    }
}
