package tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.Data;

/**
 * Classe di test per verificare il comportamento e le invarianti
 * definite nella classe astratta Node.
 */
public class NodeTest {

    private Data trainingSet;
    private Node nodo;
    private int inizio;
    private int fine;

    /**
     * Istanzia il training set globale e un nodo di base
     * da riutilizzare in tutti i test, riducendo la duplicazione del codice.
     * 
     * @throws Exception se si verificano errori di connessione al database.
     */
    @BeforeEach
    public void setUp() throws Exception {
        trainingSet = new Data("provaC");
        inizio = 0;
        fine = trainingSet.getNumberOfExamples() - 1;
        
        // Utilizziamo LeafNode come implementazione concreta per testare Node
        nodo = new LeafNode(trainingSet, inizio, fine);
    }

    /**
     * Verifica la corretta inizializzazione degli indici di inizio e fine.
     */
    @Test
    public void testInizializzazioneIndici() {
        assertEquals(inizio, nodo.getBeginExampleIndex(), "L'indice iniziale deve corrispondere a quello passato nel costruttore");
        assertEquals(fine, nodo.getEndExampleIndex(), "L'indice finale deve corrispondere a quello passato nel costruttore");
    }

    /**
     * Verifica l'assegnazione sequenziale progressiva degli identificativi numerici (idNode).
     */
    @Test
    public void testGenerazioneIdProgressivo() {
        Node secondoNodo = new LeafNode(trainingSet, inizio, fine);
        assertTrue(secondoNodo.getIdNode() > nodo.getIdNode(), "L'idNode deve essere univoco e incrementare per ogni nuova istanza");
    }

    /**
     * Verifica il calcolo della varianza (SSE) nel costruttore.
     */
    @Test
    public void testCalcoloVarianza() {
        assertTrue(nodo.getVariance() >= 0.0, "La varianza calcolata (SSE) deve essere sempre un valore maggiore o uguale a zero");
    }

    /**
     * Verifica la formattazione prodotta dal metodo toString.
     */
    @Test
    public void testToString() {
        String rappresentazione = nodo.toString();
        assertTrue(rappresentazione.contains("Examples:" + inizio + "-" + fine), "Il metodo toString deve includere il range degli indici degli esempi coperti");
        assertTrue(rappresentazione.contains("variance:"), "Il metodo toString deve stampare l'etichetta della varianza");
    }
}