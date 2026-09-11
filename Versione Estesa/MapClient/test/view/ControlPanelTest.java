package view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
    
    @Test
    public void testGetServerPortWithWhitespace() throws Exception {
        ControlPanel panel = new ControlPanel();
        
        java.lang.reflect.Field field = ControlPanel.class.getDeclaredField("portField");
        field.setAccessible(true);
        javax.swing.JTextField portField = (javax.swing.JTextField) field.get(panel);

        // Test con spazi attorno al numero
        portField.setText("  9090  ");
        try {
            int port = panel.getServerPort();
            assertEquals(9090, port, "getServerPort deve gestire gli spazi attorno al numero");
        } catch (NumberFormatException e) {
            fail("getServerPort dovrebbe applicare trim() prima del parsing");
        }

        // Test con soli spazi
        portField.setText("   ");
        assertThrows(NumberFormatException.class, () -> panel.getServerPort(),
            "Una porta con soli spazi deve sollevare NumberFormatException");
    }
}
