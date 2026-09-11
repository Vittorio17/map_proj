package view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Test unitari per la classe {@link ControlPanel}.
 * Verifica i valori di default, il recupero dei dati inseriti
 * e il comportamento dei controlli UI.
 */
class ControlPanelTest {

    private ControlPanel panel;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel = new ControlPanel());
    }

    @Test
    void testDefaultValues() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals("127.0.0.1", panel.getServerAddress());
            assertEquals(8080, panel.getServerPort());
            assertEquals("provac", panel.getTableName());
            assertFalse(panel.getPredictButton().isEnabled());
            assertTrue(panel.getLoadTreeButton().isEnabled());
            assertTrue(panel.getRefreshTablesButton().isEnabled());
        });
    }

    @Test
    void testGetServerAddress() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals("127.0.0.1", panel.getServerAddress());
        });
    }

    @Test
    void testGetServerPort() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals(8080, panel.getServerPort());
        });
    }

    @Test
    void testGetServerPortInvalid() throws Exception {
        // Imposta una porta non numerica tramite riflessione (fuori dal lambda)
        var portField = ControlPanel.class.getDeclaredField("portField");
        portField.setAccessible(true);
        JTextField pf = (JTextField) portField.get(panel);
        SwingUtilities.invokeAndWait(() -> pf.setText("abc"));
        assertThrows(NumberFormatException.class, () -> panel.getServerPort());
    }

    @Test
    void testGetTableName() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals("provac", panel.getTableName());
        });
    }

    @Test
    void testIsDatabaseSource() throws Exception {
        // Default: "Da Database" (indice 0)
        assertTrue(panel.isDatabaseSource());

        // Cambia a "Da Archivio (.dmp)" (indice 1)
        var sourceField = ControlPanel.class.getDeclaredField("sourceComboBox");
        sourceField.setAccessible(true);
        JComboBox<String> combo = (JComboBox<String>) sourceField.get(panel);
        SwingUtilities.invokeAndWait(() -> combo.setSelectedIndex(1));
        assertFalse(panel.isDatabaseSource());
    }
}
