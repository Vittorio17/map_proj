package tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.Data;
import server.UnknownValueException;

/**
 * Classe di test per l'induzione, la navigazione e la serializzazione
 * dell'albero di decisione generato dalla classe RegressionTree.
 */
public class RegressionTreeTest {

    private Data trainingSet;
    private RegressionTree tree;
    private final String FILE_NAME = "test_tree.ser";

    /**
     * Setup iniziale: carica i dati dal database e avvia la ricorsione 
     * sull'albero tramite il costruttore principale.
     * 
     * @throws Exception in caso di problemi di accesso a MySQL.
     */
    @BeforeEach
    public void setUp() throws Exception {
        trainingSet = new Data("provaC");
        tree = new RegressionTree(trainingSet);
    }

    /**
     * Pulizia post-test: elimina l'eventuale file temporaneo creato
     * durante i collaudi di serializzazione per mantenere pulito il filesystem.
     */
    @AfterEach
    public void tearDown() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Verifica la corretta generazione strutturale dell'albero al momento dell'istanziazione.
     */
    @Test
    public void testInduzioneAlbero() {
        String struttura = tree.toString();
        assertNotNull(struttura, "La rappresentazione testuale dell'albero non deve essere nulla");
        assertTrue(struttura.contains("SPLIT") || struttura.contains("LEAF"), "L'albero indotto deve contenere nodi di tipo SPLIT o LEAF");
    }

    /**
     * Verifica il comportamento dei metodi getter in presenza di un nodo radice (Split).
     */
    @Test
    public void testComportamentoNodoRadice() {
        // La radice del dataset provaC (avendo più valori) sarà un nodo di split
        assertNotNull(tree.getCurrentNodeQuery(), "Un nodo radice di tipo split deve restituire una query formulata valida");
        assertNull(tree.getPredictedValue(), "Un nodo di split non deve restituire un valore predetto diretto, per cui si attende null");
    }

    /**
     * Verifica il sollevamento dell'eccezione personalizzata in caso di input fuori range.
     */
    @Test
    public void testNavigazioneEccezione() {
        assertThrows(UnknownValueException.class, () -> tree.getChild(-1), "Fornire un indice negativo a getChild deve sollevare UnknownValueException");
    }

    /**
     * Verifica la persistenza dell'albero su file e il corretto ripristino dell'oggetto.
     * 
     * @throws Exception in caso di errori nei flussi I/O.
     */
    @Test
    public void testSerializzazione() throws Exception {
        // Salvataggio
        tree.salva(FILE_NAME);
        File file = new File(FILE_NAME);
        assertTrue(file.exists(), "Il metodo salva deve generare un file sul filesystem locale");

        // Caricamento
        RegressionTree loadedTree = RegressionTree.carica(FILE_NAME);
        assertEquals(tree.toString(), loadedTree.toString(),"L'albero ripristinato dal file deve essere strutturalmente identico a quello originale");
    }
}