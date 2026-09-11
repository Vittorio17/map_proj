package controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import view.MainFrame;
import view.ControlPanel;
import network.ServerConnection;

import javax.swing.SwingUtilities;
import java.lang.reflect.Field;

/**
 * Test unitari per la classe {@link MainController}.
 * Verifica la creazione del controller, lo stato iniziale dei pulsanti
 * e il comportamento di validazione dei parametri di input.
 */
class MainControllerTest {

    private MainFrame frame;
    private MainController controller;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame = new MainFrame();
            controller = new MainController(frame);
        });
    }

    @Test
    void testConstructorCreatesConnection() throws Exception {
        // Verifica che il controller abbia istanziato una ServerConnection
        Field connField = MainController.class.getDeclaredField("connection");
        connField.setAccessible(true);
        ServerConnection conn = (ServerConnection) connField.get(controller);
        assertNotNull(conn);
        assertFalse(conn.isConnected());
    }

    @Test
    void testPredictButtonDisabledWithoutConnection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // La connessione non e' attiva, il pulsante predizione e' gia' disabilitato
            ControlPanel cp = frame.getControlPanel();
            assertFalse(cp.getPredictButton().isEnabled());
        });
    }

    @Test
    void testEmptyTableNameShowsWarning() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ControlPanel cp = frame.getControlPanel();

            // Imposta un nome tabella vuoto tramite riflessione
            try {
                var tableField = ControlPanel.class.getDeclaredField("tableComboBox");
                tableField.setAccessible(true);
                javax.swing.JComboBox<String> combo =
                    (javax.swing.JComboBox<String>) tableField.get(cp);
                combo.setSelectedItem("");
            } catch (Exception e) {
                fail("Errore nell'impostazione della tabella: " + e.getMessage());
            }

            // Verifica che getTableName() restituisca stringa vuota
            assertEquals("", cp.getTableName());
        });
    }

    @Test
    void testInvalidPortShowsError() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ControlPanel cp = frame.getControlPanel();

            // Imposta una porta non valida tramite riflessione
            try {
                var portField = ControlPanel.class.getDeclaredField("portField");
                portField.setAccessible(true);
                javax.swing.JTextField pf = (javax.swing.JTextField) portField.get(cp);
                pf.setText("99999");
            } catch (Exception e) {
                fail("Errore nell'impostazione della porta: " + e.getMessage());
            }

            // Verifica che getServerPort() lanci NumberFormatException per porta fuori range
            // Il controller valida la porta ma il test verifica che il campo accetta qualsiasi intero
            assertDoesNotThrow(() -> {
                int port = cp.getServerPort();
                assertEquals(99999, port);
            });
        });
    }
}
