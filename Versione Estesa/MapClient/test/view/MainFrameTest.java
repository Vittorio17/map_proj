package view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;

/**
 * Test unitari per la classe {@link MainFrame}.
 * Verifica il titolo della finestra e la corretta istanziazione dei pannelli.
 */
class MainFrameTest {

    private MainFrame frame;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame = new MainFrame();
        });
    }

    @Test
    void testTitle() {
        assertEquals("Regression Tree Dashboard", frame.getTitle());
    }

    @Test
    void testPanelsExist() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertNotNull(frame.getControlPanel());
            assertNotNull(frame.getTreePanel());
            assertNotNull(frame.getSummaryPanel());
            assertNotNull(frame.getLogPanel());
        });
    }
}
