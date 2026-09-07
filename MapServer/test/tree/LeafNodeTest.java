package tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.Data;

public class LeafNodeTest {

    private LeafNode foglia;

    @BeforeEach
    public void setUp() throws Exception {
        Data trainingSet = new Data("provaC"); 
        foglia = new LeafNode(trainingSet, 0, trainingSet.getNumberOfExamples() - 1);
    }

    @Test
    public void testCalcoloValorePredetto() {
        double valorePredetto = foglia.getPredictedClassValue();
        assertTrue(!Double.isNaN(valorePredetto), "Il valore predetto calcolato dalla media deve essere un numero valido");
    }

    @Test
    public void testNumeroRamiFigli() {
        assertEquals(0, foglia.getNumberOfChildren(), "Un nodo terminale (foglia) non deve avere figli");
    }

    @Test
    public void testToString() {
        assertTrue(foglia.toString().contains("LEAF"), "L'output testuale deve etichettare esplicitamente il nodo come foglia");
    }
}