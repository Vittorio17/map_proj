package network;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Test unitari per la classe {@link PredictionWorker}.
 * Testa il metodo privato parseBranchOptions tramite riflessione.
 */
class PredictionWorkerTest {

    /**
     * Helper per invocare il metodo privato parseBranchOptions tramite riflessione.
     */
    @SuppressWarnings("unchecked")
    private List<String> invokeParseBranchOptions(String queryText) throws Exception {
        // Creiamo un'istanza minimale - il costruttore richiede ServerConnection e MainFrame,
        // ma il metodo parseBranchOptions non usa i campi dell'istanza.
        Method method = PredictionWorker.class.getDeclaredMethod("parseBranchOptions", String.class);
        method.setAccessible(true);

        // Creiamo un'istanza con parametri dummy (non utilizzati da parseBranchOptions)
        PredictionWorker worker = new PredictionWorker(null, null);
        return (List<String>) method.invoke(worker, queryText);
    }

    @Test
    void testParseBranchOptionsWithNumbers() throws Exception {
        String queryText = "0: Si\n1: No";
        List<String> options = invokeParseBranchOptions(queryText);

        assertEquals(2, options.size());
        assertEquals("Si", options.get(0));
        assertEquals("No", options.get(1));
    }

    @Test
    void testParseBranchOptionsWithoutNumbers() throws Exception {
        String queryText = "Opzione A\nOpzione B";
        List<String> options = invokeParseBranchOptions(queryText);

        assertEquals(2, options.size());
        assertEquals("Opzione A", options.get(0));
        assertEquals("Opzione B", options.get(1));
    }

    @Test
    void testParseBranchOptionsWithEmptyLines() throws Exception {
        String queryText = "0: Si\n\n1: No\n\n";
        List<String> options = invokeParseBranchOptions(queryText);

        assertEquals(2, options.size());
        assertEquals("Si", options.get(0));
        assertEquals("No", options.get(1));
    }
}
