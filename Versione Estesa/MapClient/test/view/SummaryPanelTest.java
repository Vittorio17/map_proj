package view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.util.Arrays;
import java.util.Locale;

/**
 * Test unitari per la classe {@link SummaryPanel}.
 * Verifica i metodi di aggiornamento dell'interfaccia: predizione,
 * passi, tabella, reset e cronologia.
 */
class SummaryPanelTest {

    private SummaryPanel panel;
    private JLabel predictionLabel;
    private JLabel stepsLabel;
    private JLabel tableLabel;
    private DefaultListModel<String> historyModel;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel = new SummaryPanel());
        var predField = SummaryPanel.class.getDeclaredField("predictionValueLabel");
        predField.setAccessible(true);
        predictionLabel = (JLabel) predField.get(panel);

        var stepsField = SummaryPanel.class.getDeclaredField("stepsValueLabel");
        stepsField.setAccessible(true);
        stepsLabel = (JLabel) stepsField.get(panel);

        var tableField = SummaryPanel.class.getDeclaredField("tableValueLabel");
        tableField.setAccessible(true);
        tableLabel = (JLabel) tableField.get(panel);

        var modelField = SummaryPanel.class.getDeclaredField("historyModel");
        modelField.setAccessible(true);
        historyModel = (DefaultListModel<String>) modelField.get(panel);
    }

    @Test
    void testSetPredictionWithValue() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.setPrediction(3.14));
        // Il formato dipende dalla locale del sistema (italiano usa la virgola)
        String expected = String.format(Locale.getDefault(), "%.4f", 3.14);
        assertEquals(expected, predictionLabel.getText());
    }

    @Test
    void testSetPredictionWithNull() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.setPrediction(null));
        assertEquals("---", predictionLabel.getText());
    }

    @Test
    void testSetSteps() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.setSteps(5));
        assertEquals("5", stepsLabel.getText());
    }

    @Test
    void testSetTable() throws Exception {
        SwingUtilities.invokeAndWait(() -> panel.setTable("tabella_excel"));
        assertEquals("tabella_excel", tableLabel.getText());
    }

    @Test
    void testReset() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            panel.setSteps(10);
            panel.setPrediction(99.99);
            panel.reset();
        });
        assertEquals("0", stepsLabel.getText());
        assertEquals("---", predictionLabel.getText());
    }

    @Test
    void testAddHistoryEntry() throws Exception {
        SwingUtilities.invokeAndWait(() ->
            panel.addHistoryEntry("tab1", Arrays.asList("condA", "condB"), 42.0)
        );
        assertEquals(1, historyModel.getSize());
        String entry = historyModel.get(0);
        assertTrue(entry.contains("tab1"), "La voce dovrebbe contenere il nome tabella");
        assertTrue(entry.contains("condA"), "La voce dovrebbe contenere il percorso");
        // Formato dipende dalla locale
        String expectedPred = String.format(Locale.getDefault(), "%.4f", 42.0);
        assertTrue(entry.contains(expectedPred), "La voce dovrebbe contenere il valore predetto");
    }
}
