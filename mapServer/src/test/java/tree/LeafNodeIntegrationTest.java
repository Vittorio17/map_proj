package tree;

import data.Data;
import data.TrainingDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Test di unità della classe LeafNode.
 * Prerequisiti:
 * - MySQL attivo su indirizzo 127.0.0.1, porta 3306
 * - Database MapDB presente con l'utente MapUser/map
 * - Tabella provaC creata e popolata
*/
class LeafNodeTest {


    private Data data;

    @BeforeEach
    void setUp() throws TrainingDataException {
        // Usiamo il database reale "provaC"
        data = new Data("provaC"); 
    }

    /**
     * Verifica che la Media e la Varianza della partizione vengano calcolate
     * in maniera corretta.
     */
    @Test
    @DisplayName("Verifica che la Media e la Varianza della partizione siano corrette")
    void verificaCorrettezzaMatematica() {
        // Testiamo le prime 3 righe del database (indici 0, 1, 2)
        int begin = 0;
        int end = 2;
        
        double y0 = data.getClassValue(0);
        double y1 = data.getClassValue(1);
        double y2 = data.getClassValue(2);
        
    
        double mediaAttesa = (y0 + y1 + y2) / 3.0;
        
        double errore0 = (y0 - mediaAttesa) * (y0 - mediaAttesa);
        double errore1 = (y1 - mediaAttesa) * (y1 - mediaAttesa);
        double errore2 = (y2 - mediaAttesa) * (y2 - mediaAttesa);
        double varianzaAttesa = errore0 + errore1 + errore2;

        
        LeafNode leaf = new LeafNode(data, begin, end);

        assertEquals(mediaAttesa, leaf.getPredictedClassValue(), 0.0001, 
            "La media predetta non coincide con il calcolo manuale");
            
        assertEquals(varianzaAttesa, leaf.getVariance(), 0.0001, 
            "La varianza calcolata dal Node padre non coincide con il calcolo manuale");
    }
}