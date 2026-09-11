package view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Color;

/**
 * Test unitari per la classe {@link LogPanel}.
 * Verifica la classificazione automatica dei livelli di log
 * in base alle keyword nel messaggio.
 */
class LogPanelTest {

    private LogPanel panel;
    private StyledDocument doc;
    private JTextPane textPane;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel = new LogPanel());
        var docField = LogPanel.class.getDeclaredField("doc");
        docField.setAccessible(true);
        doc = (StyledDocument) docField.get(panel);

        var tpField = LogPanel.class.getDeclaredField("textPane");
        tpField.setAccessible(true);
        textPane = (JTextPane) tpField.get(panel);
    }

    /**
     * Restituisce il colore foreground dallo stile applicato alla posizione data.
     */
    private Color getForegroundColorAt(int position) {
        AttributeSet attrs = doc.getCharacterElement(position).getAttributes();
        return StyleConstants.getForeground(attrs);
    }

    @Test
    void testLogInfoMessage() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.log("Messaggio generico di test"));
        String content = doc.getText(0, doc.getLength());
        assertTrue(content.contains("> Messaggio generico di test"));
        // Verifica colore INFO (bianco / grigio chiaro)
        Color infoColor = new Color(248, 248, 242);
        assertEquals(infoColor, getForegroundColorAt(2));
    }

    @Test
    void testLogErrorMessage() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.log("Si e' verificato un errore grave"));
        // Verifica colore ERROR (rosso)
        Color errorColor = new Color(255, 85, 85);
        assertEquals(errorColor, getForegroundColorAt(2));
    }

    @Test
    void testLogSuccessMessage() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.log("Operazione completata con successo"));
        // Verifica colore SUCCESS (verde)
        Color successColor = new Color(80, 250, 123);
        assertEquals(successColor, getForegroundColorAt(2));
    }

    @Test
    void testLogWarningMessage() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.log("Operazione annullata dall'utente"));
        // Verifica colore WARN (arancio)
        Color warnColor = new Color(255, 184, 108);
        assertEquals(warnColor, getForegroundColorAt(2));
    }

    @Test
    void testClear() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            panel.log("Messaggio da cancellare");
            panel.clear();
        });
        assertEquals("", doc.getText(0, doc.getLength()));
    }
}
